package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.domain.dto.EmailVerificationDto
import com.melowetty.hsepermhelper.domain.dto.HideLessonDto
import com.melowetty.hsepermhelper.domain.dto.RemoteScheduleLink
import com.melowetty.hsepermhelper.domain.dto.SettingsDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.entity.HideLessonEntity
import com.melowetty.hsepermhelper.domain.entity.UserEntity
import com.melowetty.hsepermhelper.domain.model.event.EmailIsVerifiedEvent
import com.melowetty.hsepermhelper.exception.UserIsExistsException
import com.melowetty.hsepermhelper.exception.UserNotFoundException
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toDto
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toEntity
import com.melowetty.hsepermhelper.notification.EmailIsVerifiedNotification
import com.melowetty.hsepermhelper.repository.HiddenLessonRepository
import com.melowetty.hsepermhelper.repository.UserRepository
import com.melowetty.hsepermhelper.service.EmailVerificationService
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.RemoteScheduleService
import com.melowetty.hsepermhelper.service.UserService
import com.melowetty.hsepermhelper.validation.annotation.ValidHseEmail
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.util.UUID
import kotlin.jvm.optionals.getOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.util.UriComponentsBuilder

@Service
@Validated
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val hiddenLessonRepository: HiddenLessonRepository,
    private val remoteScheduleService: RemoteScheduleService,
    private val emailVerificationService: EmailVerificationService,
    private val notificationService: NotificationService
) : UserService {
    @Value("\${remote-schedule.connect-url}")
    private lateinit var remoteScheduleConnectUrl: String

    override fun getByTelegramId(telegramId: Long): UserDto {
        return getUserEntityByTelegramId(telegramId).toDto()
    }

    override fun getById(id: UUID): UserDto {
        val user = userRepository.findById(id)
        if (user.isEmpty) throw UserNotFoundException("Пользователь с таким ID не найден!")
        return user.get().toDto()
    }

    override fun create(dto: UserDto): UserDto {
        val isExists = userRepository.existsByTelegramId(dto.telegramId)
        if (isExists) throw UserIsExistsException("Пользователь с таким Telegram ID уже существует!")
        val user = userRepository.save(dto.toEntity()).toDto()
        return user
    }

    override fun deleteById(id: UUID) {
        val user = userRepository.findById(id)
        if (user.isEmpty) throw UserNotFoundException("Пользователь с таким ID не найден!")
        userRepository.delete(user.get())
    }

    override fun deleteByTelegramId(telegramId: Long) {
        val user = getUserEntityByTelegramId(telegramId)
        userRepository.delete(user)
    }

    override fun updateUser(user: UserDto): UserDto {
        val userId = getByTelegramId(user.telegramId).id
        val newUser = userRepository.save(
            user.copy(id = userId).toEntity()
        ).toDto()

        return newUser
    }

    override fun updateUserSettings(telegramId: Long, settings: SettingsDto): UserDto {
        val user = getByTelegramId(telegramId)
        val newUser = userRepository.save(
            user.copy(
                settings = settings
            ).toEntity()
        ).toDto()

        return newUser
    }

    override fun updateUserSettings(telegramId: Long, settings: Map<String, Any?>): UserDto {
        val user = getByTelegramId(telegramId)
        val userSettings = user.settings.copy()
        val newSettings = settings.toMutableMap()
        newSettings.remove("id")
        newSettings.forEach { (t, u) ->
            val field = ReflectionUtils.findField(SettingsDto::class.java, t)
            if (field != null) {
                field.trySetAccessible()
                ReflectionUtils.setField(field, userSettings, u)
            }
        }
        return updateUserSettings(telegramId, userSettings)
    }

    override fun addHiddenLesson(telegramId: Long, lesson: HideLessonDto): UserDto {
        val user = getUserEntityByTelegramId(telegramId)
        val hiddenLessons = user.settings.hiddenLessons.toMutableSet()

        if (hiddenLessons.any {
                it.lesson == lesson.lesson
                        && it.lessonType == lesson.lessonType
                        && it.subGroup == lesson.subGroup
            }) {
            throw RuntimeException("Эта пара уже скрыта!")
        }

        val lessonEntity = hiddenLessonRepository.save(
            HideLessonEntity(
                lesson = lesson.lesson,
                lessonType = lesson.lessonType,
                subGroup = lesson.subGroup
            )
        )

        hiddenLessons.add(lessonEntity)

        return userRepository.save(
            user.copy(
                settings = user.settings.copy(
                    hiddenLessons = hiddenLessons,
                )
            )
        ).toDto()
    }

    override fun removeHiddenLesson(telegramId: Long, lesson: HideLessonDto): UserDto {
        val user = getUserEntityByTelegramId(telegramId)
        val hiddenLessons = user.settings.hiddenLessons.toMutableSet()

        hiddenLessons.removeIf {
            if (it.lesson == lesson.lesson
                && it.lessonType == lesson.lessonType
                && it.subGroup == lesson.subGroup
            ) {
                hiddenLessonRepository.delete(it)
                return@removeIf true
            }
            return@removeIf false
        }

        return userRepository.save(
            user.copy(
                settings = user.settings.copy(
                    hiddenLessons = hiddenLessons,
                )
            )
        ).toDto()
    }

    override fun clearHiddenLessons(telegramId: Long): UserDto {
        val user = getUserEntityByTelegramId(telegramId)

        hiddenLessonRepository.deleteAllInBatch(user.settings.hiddenLessons)

        return userRepository.save(
            user.copy(
                settings = user.settings.copy(hiddenLessons = setOf())
            )
        ).toDto()
    }

    override fun getRemoteScheduleLink(telegramId: Long): RemoteScheduleLink {
        try {
            val token = remoteScheduleService.getUserScheduleToken(telegramId).token

            return RemoteScheduleLink(
                direct = generateRemoteScheduleConnectLink(token)
            )
        } catch (e: RuntimeException) {
            return createOrUpdateScheduleLink(telegramId)
        }
    }

    override fun createOrUpdateScheduleLink(telegramId: Long): RemoteScheduleLink {
        val token = remoteScheduleService.createOrUpdateUserScheduleToken(telegramId).token

        return RemoteScheduleLink(
            direct = generateRemoteScheduleConnectLink(token)
        )
    }

    override fun setOrUpdateEmailRequest(telegramId: Long,
                                         @Valid @ValidHseEmail email: String
    ): EmailVerificationDto {
        return emailVerificationService.startVerificationProcess(telegramId, email)
    }

    override fun deleteEmail(telegramId: Long) {
        val user = getUserEntityByTelegramId(telegramId)
        userRepository.save(
            user.copy(email = null)
        )
    }

    @EventListener(EmailIsVerifiedEvent::class)
    fun handleEmailVerifiedEvent(event: EmailIsVerifiedEvent) {
        val user = userRepository.findById(event.userId).getOrNull()
            ?: return

        userRepository.save(
            user.copy(email = event.email)
        )

        notificationService.sendNotificationV2(
            EmailIsVerifiedNotification(user.telegramId)
        )
    }

    private fun generateRemoteScheduleConnectLink(token: String): String {
        return UriComponentsBuilder.fromUriString(remoteScheduleConnectUrl)
            .queryParam("token", token)
            .encode()
            .toUriString()
    }

    override fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { it.toDto() }
    }

    private fun getUserEntityByTelegramId(telegramId: Long): UserEntity {
        return userRepository.findByTelegramId(telegramId)
            .orElseThrow { UserNotFoundException("Пользователь с таким telegram ID не найден!") }
    }
}