package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.dto.UserDto
import com.melowetty.hsepermhelper.dto.UserEventDto
import com.melowetty.hsepermhelper.entity.UserEventEntity
import com.melowetty.hsepermhelper.models.UserEventType
import com.melowetty.hsepermhelper.repository.UserEventRepository
import com.melowetty.hsepermhelper.service.UserEventService
import com.melowetty.hsepermhelper.service.UserService
import com.melowetty.hsepermhelper.service.impl.UserServiceImpl.Companion.toDto
import com.melowetty.hsepermhelper.service.impl.UserServiceImpl.Companion.toEntity
import org.springframework.stereotype.Service

@Service
class UserEventServiceImpl(
    private val userEventRepository: UserEventRepository,
    private val userService: UserService,
): UserEventService {
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

    companion object {
        fun UserEventDto.toEntity(): UserEventEntity {
            return UserEventEntity(
                id = id,
                date = date,
                targetUser = targetUser.toEntity(),
                userEventType = userEventType,
            )
        }

        fun UserEventEntity.toDto(): UserEventDto {
            return UserEventDto(
                id = id,
                date = date,
                targetUser = targetUser.toDto(),
                userEventType = userEventType,
            )
        }
    }
}