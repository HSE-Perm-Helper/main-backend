package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.domain.dto.SettingsDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.event.EventType
import com.melowetty.hsepermhelper.event.UsersChangedEvent
import com.melowetty.hsepermhelper.exception.UserIsExistsException
import com.melowetty.hsepermhelper.exception.UserNotFoundException
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toDto
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toEntity
import com.melowetty.hsepermhelper.repository.UserRepository
import com.melowetty.hsepermhelper.service.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils
import java.util.*

@Service
class UserServiceImpl(
    private val eventPublisher: ApplicationEventPublisher,
    private val userRepository: UserRepository,
): UserService {
    override fun getByTelegramId(telegramId: Long): UserDto {
        val user = userRepository.findByTelegramId(telegramId)
        if(user.isEmpty) throw UserNotFoundException("Пользователь с таким Telegram ID не найден!")
        return user.get().toDto()
    }

    override fun getById(id: UUID): UserDto {
        val user = userRepository.findById(id)
        if(user.isEmpty) throw UserNotFoundException("Пользователь с таким ID не найден!")
        return user.get().toDto()
    }

    override fun create(dto: UserDto): UserDto {
        val isExists = userRepository.existsByTelegramId(dto.telegramId)
        if(isExists) throw UserIsExistsException("Пользователь с таким Telegram ID уже существует!")
        val user = userRepository.save(dto.toEntity()).toDto()
        val event = UsersChangedEvent(
            user = user,
            type = EventType.ADDED
        )
        eventPublisher.publishEvent(event)
        return user
    }

    override fun deleteById(id: UUID) {
        val user = userRepository.findById(id)
        if (user.isEmpty) throw UserNotFoundException("Пользователь с таким ID не найден!")
        userRepository.delete(user.get())
        val event = UsersChangedEvent(
            user = user.get().toDto(),
            type = EventType.DELETED
        )
        eventPublisher.publishEvent(event)
    }

    override fun deleteByTelegramId(telegramId: Long) {
        val user = userRepository.findByTelegramId(telegramId)
        if (user.isEmpty) throw UserNotFoundException("Пользователь с таким Telegram ID не найден!")
        userRepository.delete(user.get())
        val event = UsersChangedEvent(
            user = user.get().toDto(),
            type = EventType.DELETED
        )
        eventPublisher.publishEvent(event)
    }

    override fun updateUser(user: UserDto): UserDto {
        val userId = getByTelegramId(user.telegramId).id
        val newUser = userRepository.save(
            user.copy(id = userId).toEntity()
        ).toDto()

        val event = UsersChangedEvent(
            user = newUser,
            type = EventType.EDITED
        )
        eventPublisher.publishEvent(event)
        return newUser
    }

    override fun updateUserSettings(telegramId: Long, settings: SettingsDto): UserDto {
        val user = getByTelegramId(telegramId)
        val newUser = userRepository.save(
            user.copy(settings = settings).toEntity()
        ).toDto()

        val event = UsersChangedEvent(
            user = newUser,
            type = EventType.EDITED
        )
        eventPublisher.publishEvent(event)
        return newUser
    }

    override fun updateUserSettings(telegramId: Long, settings: Map<String, Any?>): UserDto {
        val user = getByTelegramId(telegramId)
        val userSettings = user.settings.copy()
        val newSettings = settings.toMutableMap()
        newSettings.remove("id")
        newSettings.forEach { t, u ->
            val field = ReflectionUtils.findField(SettingsDto::class.java, t)
            if (field != null) {
                field.trySetAccessible()
                ReflectionUtils.setField(field, userSettings, u)
            }
        }
        return updateUserSettings(telegramId, userSettings)
    }

    override fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { it.toDto() }
    }

    override fun getAllUsers(group: String, subGroup: Int): List<UserDto> {
        return userRepository.findAllBySettingsGroupAndSettingsSubGroup(group, subGroup).map { it.toDto() }
    }
}