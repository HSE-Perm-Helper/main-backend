package com.melowetty.hsepermhelper.service

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
}