package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.dto.UserEventDto
import com.melowetty.hsepermhelper.extension.UserEventExtensions.Companion.toDto
import com.melowetty.hsepermhelper.extension.UserEventExtensions.Companion.toEntity
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toEntity
import com.melowetty.hsepermhelper.model.UserEventType
import com.melowetty.hsepermhelper.repository.UserEventRepository
import com.melowetty.hsepermhelper.service.UserEventService
import com.melowetty.hsepermhelper.service.UserService
import org.springframework.stereotype.Service

@Service
class UserEventServiceImpl(
    private val userEventRepository: UserEventRepository,
    private val userService: UserService,
) : UserEventService {
    override fun addUserEvent(telegramId: Long, eventType: UserEventType) {
        val user = userService.getByTelegramId(telegramId)
        return addUserEvent(user, eventType)
    }

    override fun addUserEvent(user: UserDto, eventType: UserEventType) {
        userEventRepository.save(
            UserEventDto(
                targetUser = user,
                userEventType = eventType,
            ).toEntity()
        )
    }

    override fun getAllUserEvents(user: UserDto): List<UserEventDto> {
        return userEventRepository.findByTargetUser(user.toEntity()).map { it.toDto() }
    }

    override fun getAllUserEvents(user: UserDto, eventType: UserEventType): List<UserEventDto> {
        return userEventRepository.findByTargetUserAndUserEventType(user.toEntity(), eventType).map { it.toDto() }
    }
}