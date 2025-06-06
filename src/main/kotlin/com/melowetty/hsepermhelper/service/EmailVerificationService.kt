package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.EmailVerificationDto
import com.melowetty.hsepermhelper.domain.entity.EmailVerificationEntity
import com.melowetty.hsepermhelper.domain.model.event.EmailIsVerifiedEvent
import com.melowetty.hsepermhelper.notification.verification.EmailVerificationSendNotification
import com.melowetty.hsepermhelper.exception.UserNotFoundException
import com.melowetty.hsepermhelper.exception.verification.ReachMaxAttemptsToVerificationRequestException
import com.melowetty.hsepermhelper.exception.verification.VerificationNotFoundOrExpiredException
import com.melowetty.hsepermhelper.exception.verification.VerificationRequestNotFoundException
import com.melowetty.hsepermhelper.exception.verification.VerificationRequestYetNotReadyForResendException
import com.melowetty.hsepermhelper.repository.EmailVerificationRepository
import com.melowetty.hsepermhelper.repository.UserRepository
import java.time.LocalDateTime
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class EmailVerificationService(
    private val emailVerificationRepository: EmailVerificationRepository,
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val notificationService: NotificationService
) {
    @Value("\${email-verification.base-url}")
    private lateinit var baseUrl: String

    companion object {
        private const val TOKEN_LENGTH = 10
        private const val SECRET_LENGTH = 24
        private const val ATTEMPT_STEP_IN_SECONDS = 60L
        private const val MAX_ATTEMPTS_COUNT = 3
    }

    fun startVerificationProcess(telegramId: Long, email: String): EmailVerificationDto {
        val user = userRepository.findByTelegramId(telegramId).orElseThrow {
            UserNotFoundException("Пользователь с таким ID не найден")
        }

        val existsVerification = emailVerificationRepository.findByUser(user)
        
        existsVerification?.let {
            emailVerificationRepository.delete(existsVerification)
        }


        val currentDate = LocalDateTime.now()

        val entity = EmailVerificationEntity(
            token = generateRandomString(TOKEN_LENGTH),
            secret = generateRandomString(SECRET_LENGTH),
            email = email,
            user = user,
            attempts = 1,
            nextAttempt = currentDate.plusSeconds(ATTEMPT_STEP_IN_SECONDS),
            created = LocalDateTime.now()
        )

        emailVerificationRepository.save(entity)

        notificationService.sendNotificationV2(
            EmailVerificationSendNotification(email, generateVerificationLink(entity.secret))
        )

        return entity.toDto()
    }

    fun checkVerificationSecret(secret: String) {
        val verification = emailVerificationRepository.findBySecret(secret)
            ?: throw VerificationNotFoundOrExpiredException()

        emailVerificationRepository.delete(verification)

        val user = verification.user
        val email = verification.email

        eventPublisher.publishEvent(EmailIsVerifiedEvent(
            userId = user.id,
            email = email,
        ))
    }

    fun cancelVerification(token: String) {
        emailVerificationRepository.deleteById(token)
    }

    fun getVerificationByToken(token: String): EmailVerificationDto {
        val verification = emailVerificationRepository.findByToken(token)
            ?: throw VerificationRequestNotFoundException()

        return verification.toDto()
    }

    fun resendVerificationLink(token: String): EmailVerificationDto {
        val verification = emailVerificationRepository.findByToken(token)
            ?: throw VerificationRequestNotFoundException()

        if (verification.nextAttempt == null) {
            throw ReachMaxAttemptsToVerificationRequestException()
        }

        if (verification.nextAttempt!! > LocalDateTime.now()) {
            throw VerificationRequestYetNotReadyForResendException()
        }

        verification.attempts += 1

        if (verification.attempts < MAX_ATTEMPTS_COUNT) {
            val currentDate = LocalDateTime.now()
            verification.nextAttempt = currentDate.plusSeconds(ATTEMPT_STEP_IN_SECONDS)
        } else {
            verification.nextAttempt = null
        }

        notificationService.sendNotificationV2(
            EmailVerificationSendNotification(verification.email, generateVerificationLink(verification.secret))
        )

        emailVerificationRepository.save(verification)

        return verification.toDto()
    }

    private fun generateVerificationLink(secret: String): String {
        return "$baseUrl/verify/$secret"
    }

    private fun EmailVerificationEntity.toDto(): EmailVerificationDto {
        return EmailVerificationDto(
            token = token,
            nextAttempt = nextAttempt
        )
    }

    private fun generateRandomString(length: Int): String {
        return RandomStringUtils.randomAlphanumeric(length)
    }
}