package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.user.UserCreateRequest
import com.melowetty.hsepermhelper.domain.dto.EmailVerificationDto
import com.melowetty.hsepermhelper.domain.dto.ApiUserHideLesson
import com.melowetty.hsepermhelper.domain.dto.RemoteScheduleLink
import com.melowetty.hsepermhelper.domain.dto.SettingsDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.validation.annotation.ValidHseEmail
import jakarta.validation.Valid
import java.util.UUID

@Deprecated("Remove telegramId from all methods")
interface OldUserService {
    /**
     * Method returns user by he/she telegram ID
     * @param telegramId telegram ID of user
     * @return returns user object when it is found or null else
     */
    fun getByTelegramId(telegramId: Long): UserDto

    /**
     * Method returns user by he/she ID
     * @param id ID of user
     * @return returns user object when it is found or null else
     */
    fun getById(id: UUID): UserDto

    fun create(request: UserCreateRequest): UserDto

    // todo: сделать пагинацию
    /**
     * Returns list of all users
     *
     * @return list of users
     */
    fun getAllUsers(): List<UserDto>

    /**
     * Delete user by id
     *
     * @param id user UUID
     */
    fun deleteById(id: UUID)

    /**
     * Delete user by telegram id
     *
     * @param telegramId user telegram id
     */
    fun deleteByTelegramId(telegramId: Long)

    /**
     * Full update user
     *
     * @param user new user data
     * @return new user object
     */
    fun updateUser(user: UserDto): UserDto

    /**
     * Update user settings
     *
     * @param telegramId user telegram id
     * @param settings new user settings
     * @return new user object
     */
    fun updateUserSettings(telegramId: Long, settings: SettingsDto): UserDto

    /**
     * Update user settings by patch method
     *
     * @param telegramId user telegram id
     * @param settings new user settings
     * @return new user object
     */
    fun updateUserSettings(telegramId: Long, settings: Map<String, Any?>): UserDto


    fun addHiddenLesson(telegramId: Long, lesson: ApiUserHideLesson): UserDto

    fun removeHiddenLesson(telegramId: Long, lesson: ApiUserHideLesson): UserDto

    fun clearHiddenLessons(telegramId: Long): UserDto

    fun getRemoteScheduleLink(telegramId: Long): RemoteScheduleLink

    fun createOrUpdateScheduleLink(telegramId: Long): RemoteScheduleLink

    fun setOrUpdateEmailRequest(telegramId: Long, @Valid @ValidHseEmail email: String): EmailVerificationDto

    fun deleteEmail(telegramId: Long)
}