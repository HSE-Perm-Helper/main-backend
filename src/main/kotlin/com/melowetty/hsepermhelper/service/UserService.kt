package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.exception.user.UserByTelegramIdNotFoundException
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toDto
import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userStorage: UserStorage,
) {
    fun getUsersById(ids: List<UUID>): List<UserDto> {
        return userStorage.getUsersById(ids).map { it.toDto() }
    }

    fun getUserByTelegramId(telegramId: Long): UserDto {
        return getUserRecordByTelegramId(telegramId).toDto()
    }

    fun getUserById(id: UUID): UserDto {
        return getUserRecordById(id).toDto()
    }

    fun getUserRecordByTelegramId(telegramId: Long): UserRecord {
        return userStorage.findUserByTelegramId(telegramId)
            ?: throw UserByTelegramIdNotFoundException(telegramId)
    }

    fun getUserRecordById(id: UUID): UserRecord {
        return userStorage.findUserById(id) ?: throw UserByIdNotFoundException(id)
    }
}