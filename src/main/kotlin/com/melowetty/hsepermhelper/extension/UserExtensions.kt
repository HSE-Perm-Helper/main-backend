package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.domain.dto.SettingsDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.entity.SettingsEntity
import com.melowetty.hsepermhelper.domain.entity.UserEntity

class UserExtensions {
    companion object {
        fun UserEntity.toDto(): UserDto {
            return UserDto(
                id = id,
                telegramId = telegramId,
                settings = settings.toDto(),
            )
        }

        fun UserDto.toEntity(): UserEntity {
            return UserEntity(
                id = id,
                telegramId = telegramId,
                settings = settings.toEntity(),
            )
        }

        fun SettingsDto.toEntity(): SettingsEntity {
            return SettingsEntity(
                id = id,
                group = group,
                subGroup = subGroup,
                includeCommonEnglish = includeCommonEnglish,
                includeCommonMinor = includeCommonMinor,
                isEnabledNewScheduleNotifications = isEnabledNewScheduleNotifications,
                isEnabledChangedScheduleNotifications = isEnabledChangedScheduleNotifications,
                isEnabledComingLessonsNotifications = isEnabledComingLessonsNotifications,
            )
        }

        fun SettingsEntity.toDto(): SettingsDto {
            return SettingsDto(
                id = id,
                group = group,
                subGroup = subGroup,
                includeCommonEnglish = includeCommonEnglish,
                includeCommonMinor = includeCommonMinor,
                isEnabledNewScheduleNotifications = isEnabledNewScheduleNotifications,
                isEnabledChangedScheduleNotifications = isEnabledChangedScheduleNotifications,
                isEnabledComingLessonsNotifications = isEnabledComingLessonsNotifications,
            )
        }
    }
}