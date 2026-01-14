package com.melowetty.hsepermhelper.service.user

import com.melowetty.hsepermhelper.controller.request.ApiUserUpdateRequest
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.domain.model.user.UserChangeRequest
import com.melowetty.hsepermhelper.domain.model.user.UserCreateRequest
import com.melowetty.hsepermhelper.domain.model.user.UserRole
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.exception.user.UserIsExistsException
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toDto
import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import com.melowetty.hsepermhelper.service.TimetableInfoService
import com.melowetty.hsepermhelper.timetable.model.EducationType
import java.util.UUID
import java.util.concurrent.ExecutorService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userStorage: UserStorage,
    private val timetableInfoService: TimetableInfoService,
    private val userEventService: UserEventService,
    @Qualifier("add-user-events-executor-service")
    private val executorService: ExecutorService,
) {
    fun updateUser(userId: UUID, request: ApiUserUpdateRequest): UserDto {
        val group = request.group
        val changedGroup = group != null
        var educationType: EducationType? = null

        if (changedGroup) {
            educationType = timetableInfoService.getEducationTypeByGroup(group)
        }

        val request = UserChangeRequest(
            group = request.group,
            educationType = educationType,
            isEnabledNewScheduleNotifications = request.isEnabledNewScheduleNotifications,
            isEnabledChangedScheduleNotifications = request.isEnabledChangedScheduleNotifications,
            isEnabledComingLessonsNotifications = request.isEnabledComingLessonsNotifications
        )

        val result = userStorage.changeUser(userId, request).toDto()

        if (changedGroup) {
            executorService.submit {
                userEventService.addUserEvent(userId, UserEventType.CHANGE_GROUP)
            }
        }

        return result
    }

    fun createUser(request: UserCreateRequest): UserDto {
        checkUserNotExistsByTelegramIdOrThrow(request.telegramId)

        val record = userStorage.createUser(
            telegramId = request.telegramId,
            group = request.group,
            educationType = timetableInfoService.getEducationTypeByGroup(request.group),
            isEnabledNewScheduleNotifications = true,
            isEnabledChangedScheduleNotifications = true,
            isEnabledComingLessonsNotifications = false,
            roles = listOf(UserRole.USER)
        )

        return record.toDto()
    }

    fun getUsersById(ids: List<UUID>): List<UserDto> {
        return userStorage.getUsersById(ids).map { it.toDto() }
    }

    fun getUserById(id: UUID): UserDto {
        return getUserRecordById(id).toDto()
    }

    fun getUserRecordById(id: UUID): UserRecord {
        return userStorage.findUserById(id) ?: throw UserByIdNotFoundException(id)
    }

    private fun checkUserNotExistsByTelegramIdOrThrow(telegramId: Long) {
        if (userStorage.existsUserByTelegramId(telegramId)) {
            throw UserIsExistsException("Пользователь с таким Telegram ID уже существует!")
        }
    }
}