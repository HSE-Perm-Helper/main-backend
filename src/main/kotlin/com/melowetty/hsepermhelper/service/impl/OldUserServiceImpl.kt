package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.domain.dto.ApiUserHideLesson
import com.melowetty.hsepermhelper.domain.dto.EmailVerificationDto
import com.melowetty.hsepermhelper.domain.dto.RemoteScheduleLink
import com.melowetty.hsepermhelper.domain.dto.SettingsDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.event.EmailIsVerifiedEvent
import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.domain.model.user.EducationGroupEntity
import com.melowetty.hsepermhelper.domain.model.user.UserCreateRequest
import com.melowetty.hsepermhelper.domain.model.user.UserRole
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.exception.user.UserByTelegramIdNotFoundException
import com.melowetty.hsepermhelper.exception.user.UserIsExistsException
import com.melowetty.hsepermhelper.exception.user.UserNotFoundException
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toDto
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toEntity
import com.melowetty.hsepermhelper.messaging.event.notification.verification.EmailIsVerifiedNotification
import com.melowetty.hsepermhelper.persistence.entity.UserEntity
import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.persistence.repository.UserRepository
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import com.melowetty.hsepermhelper.service.EmailVerificationService
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.OldUserService
import com.melowetty.hsepermhelper.service.RemoteScheduleService
import com.melowetty.hsepermhelper.service.UserEventService
import com.melowetty.hsepermhelper.service.UserHiddenLessonService
import com.melowetty.hsepermhelper.timetable.model.EducationType
import com.melowetty.hsepermhelper.util.Paginator
import com.melowetty.hsepermhelper.validation.annotation.ValidHseEmail
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils
import org.springframework.validation.annotation.Validated
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
@Validated
@Deprecated("Migrate to new user service")
class OldUserServiceImpl(
    private val userRepository: UserRepository,
    private val userStorage: UserStorage,
    private val remoteScheduleService: RemoteScheduleService,
    private val emailVerificationService: EmailVerificationService,
    private val notificationService: NotificationService,
    private val userEventService: UserEventService,
    private val userHiddenLessonService: UserHiddenLessonService,
) : OldUserService {
    @Value("\${remote-schedule.connect-url}")
    private lateinit var remoteScheduleConnectUrl: String

    override fun getByTelegramId(telegramId: Long): UserDto {
        val user = getUserRecordByTelegramId(telegramId)

        return user.toDto()
    }

    override fun getById(id: UUID): UserDto {
        val user = getUserRecordById(id)

        return user.toDto()
    }

    // TODO: исправить запрос на создание пользователя с запросом Education Type по его группе
    override fun create(request: UserCreateRequest): UserDto {
        val isExists = userRepository.existsByTelegramId(request.telegramId)
        if (isExists) {
            throw UserIsExistsException("Пользователь с таким Telegram ID уже существует!")
        }

        val user = UserEntity(
            telegramId = request.telegramId,
            educationGroup = EducationGroupEntity(
                group = request.group,
                educationType = EducationType.BACHELOR_OFFLINE,
            ),
            isEnabledNewScheduleNotifications = true,
            isEnabledChangedScheduleNotifications = true,
            isEnabledComingLessonsNotifications = false,
            roles = listOf(UserRole.USER)
        )

        return userRepository.save(user).toDto(
            listOf()
        )
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
        ).toDtoWithFetchHiddenLessons()

        return newUser
    }

    override fun updateUserSettings(telegramId: Long, settings: SettingsDto): UserDto {
        val user = getByTelegramId(telegramId)
        val newUser = userRepository.save(
            user.copy(
                settings = settings
            ).toEntity()
        ).toDtoWithFetchHiddenLessons()

        return newUser
    }

    override fun updateUserSettings(telegramId: Long, settings: Map<String, Any?>): UserDto {
        val user = getByTelegramId(telegramId)
        val userSettings = user.settings.copy()
        val newSettings = settings.toMutableMap()

        if (newSettings.containsKey("group")) {
            userEventService.addUserEvent(telegramId, UserEventType.CHANGE_GROUP)
        }

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

    override fun addHiddenLesson(telegramId: Long, lesson: ApiUserHideLesson): UserDto {
        val user = getUserRecordByTelegramId(telegramId)

        userHiddenLessonService.hideLesson(user.id, lesson.lesson, lesson.lessonType, lesson.subGroup)

        val hiddenLessons = userHiddenLessonService.getUserHiddenLessons(user.id)
        val newUser = user.copy(hiddenLessons = hiddenLessons)

        return newUser.toDto()
    }

    override fun removeHiddenLesson(telegramId: Long, lesson: ApiUserHideLesson): UserDto {
        val user = getUserRecordByTelegramId(telegramId)

        userHiddenLessonService.unHideLesson(user.id, lesson.lesson, lesson.lessonType, lesson.subGroup)

        val hiddenLessons = userHiddenLessonService.getUserHiddenLessons(user.id)
        val newUser = user.copy(hiddenLessons = hiddenLessons)

        return newUser.toDto()
    }

    override fun clearHiddenLessons(telegramId: Long): UserDto {
        val user = getUserRecordByTelegramId(telegramId)

        userHiddenLessonService.clearHiddenLessons(user.id)

        val clearedUser = user.copy(hiddenLessons = listOf())

        return clearedUser.toDto()
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
        val normalizedEmail = email.lowercase()
        val isExists = userRepository.existsByEmail(normalizedEmail)

        if(isExists) {
            throw UserIsExistsException("Пользователь с такой почтой уже есть")
        }
        
        return emailVerificationService.startVerificationProcess(telegramId, normalizedEmail)
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

        notificationService.sendUserNotification(
            event.userId,
            EmailIsVerifiedNotification()
        )
    }

    private fun generateRemoteScheduleConnectLink(token: String): String {
        return UriComponentsBuilder.fromUriString(remoteScheduleConnectUrl)
            .queryParam("token", token)
            .encode()
            .toUriString()
    }

    @Deprecated("Use projection")
    private fun getUserEntityByTelegramId(telegramId: Long): UserEntity {
        return userRepository.findByTelegramId(telegramId).getOrNull()
            ?: throw UserByTelegramIdNotFoundException(telegramId)
    }

    private fun getUserRecordByTelegramId(telegramId: Long): UserRecord {
        return userStorage.findUserByTelegramId(telegramId)
            ?: throw UserByTelegramIdNotFoundException(telegramId)
    }

    private fun getUserRecordById(id: UUID): UserRecord {
        return userStorage.findUserById(id) ?: throw UserByIdNotFoundException(id)
    }

    private fun UserEntity.toDtoWithFetchHiddenLessons(): UserDto {
        return toDto(userHiddenLessonService.getUserHiddenLessons(id))
    }

    override fun getAllUsers(): List<UserDto> {
        val result = mutableListOf<UserDto>()
        Paginator.fetchPageable(
            fetchFunction = { limit, token ->
                val options = UserStorage.Options(
                    withRoles = true,
                    withHiddenLessons = true
                )
                userStorage.findUsersAfterId(token, limit, options = options)
            }
        ) { users ->
            result.addAll(users.map { it.toDto() })
        }

        return result
    }
}