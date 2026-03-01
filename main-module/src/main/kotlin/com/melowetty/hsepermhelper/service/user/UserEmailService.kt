package com.melowetty.hsepermhelper.service.user

import com.melowetty.hsepermhelper.domain.dto.EmailVerificationDto
import com.melowetty.hsepermhelper.domain.model.event.EmailIsVerifiedEvent
import com.melowetty.hsepermhelper.domain.model.user.UserChangeRequest
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.exception.user.UserIsExistsException
import com.melowetty.hsepermhelper.messaging.event.notification.verification.EmailIsVerifiedNotification
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import com.melowetty.hsepermhelper.service.EmailVerificationService
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.validation.annotation.ValidHseEmail
import jakarta.validation.Valid
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserEmailService(
    private val userStorage: UserStorage,
    private val emailVerificationService: EmailVerificationService,
    private val notificationService: NotificationService,
) {
    fun setOrUpdateEmailRequest(
        id: UUID,
        @Valid @ValidHseEmail email: String
    ): EmailVerificationDto {
        if (!userStorage.existsUserById(id)) {
            throw UserByIdNotFoundException(id)
        }

        val normalizedEmail = email.lowercase()
        val isExists = userStorage.existsUserByEmail(normalizedEmail)

        if(isExists) {
            throw UserIsExistsException("Пользователь с такой почтой уже есть")
        }

        return emailVerificationService.startVerificationProcess(id, normalizedEmail)
    }

    fun deleteEmail(id: UUID) {
        if (!userStorage.existsUserById(id)) {
            throw UserByIdNotFoundException(id)
        }

        val request = UserChangeRequest(
            emailPresent = true,
            email = null
        )

        userStorage.changeUser(id, request)
    }

    @EventListener(EmailIsVerifiedEvent::class)
    fun handleEmailVerifiedEvent(event: EmailIsVerifiedEvent) {
        if (!userStorage.existsUserById(event.userId)) return

        val request = UserChangeRequest(
            emailPresent = true,
            email = event.email
        )

        userStorage.changeUser(event.userId, request)

        notificationService.sendUserNotification(
            event.userId,
            EmailIsVerifiedNotification()
        )
    }
}