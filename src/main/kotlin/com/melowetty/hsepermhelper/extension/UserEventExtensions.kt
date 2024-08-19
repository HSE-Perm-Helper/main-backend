package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.dto.UserEventDto
import com.melowetty.hsepermhelper.entity.UserEventEntity
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toDto
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toEntity

class UserEventExtensions {
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