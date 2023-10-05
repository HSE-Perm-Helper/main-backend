package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.dto.SettingsDto
import com.melowetty.hsepermhelper.dto.UserDto
import java.util.*

interface UserService {

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

    /**
     * Method creates user and return telegram ID when operation have success
     * @param dto User object
     * @return telegram ID
     */
    fun create(dto: UserDto): UserDto

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
    fun updateUserSettings(telegramId: Long, settings: Map<String, Any>): UserDto

    /**
     * Get all users by group and subgroup
     *
     * @param group user group
     * @param subGroup user subgroup
     * @return filtered user by group and subgroup
     */
    fun getAllUsers(group: String, subGroup: Int): List<UserDto>
}