package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.domain.dto.SettingsDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.entity.SettingsEntity
import com.melowetty.hsepermhelper.domain.entity.UserEntity
import com.melowetty.hsepermhelper.extension.HideLessonExtension.Companion.toDto
import com.melowetty.hsepermhelper.extension.HideLessonExtension.Companion.toEntity

class UserExtensions {
    companion object {
        fun UserEntity.toDto(): UserDto {
            return UserDto(
                id = id,
                telegramId = telegramId,
                settings = settings.toDto(),
                createdDate = createdDate,
                email = email
            )
        }

        fun UserDto.toEntity(): UserEntity {
            return UserEntity(
                id = id,
                telegramId = telegramId,
                settings = settings.toEntity(),
                createdDate = createdDate,
                email = email
            )
        }

        fun SettingsDto.toEntity(): SettingsEntity {
            return SettingsEntity(
                id = id,
                group = group,
                isEnabledNewScheduleNotifications = isEnabledNewScheduleNotifications,
                isEnabledChangedScheduleNotifications = isEnabledChangedScheduleNotifications,
                isEnabledComingLessonsNotifications = isEnabledComingLessonsNotifications,
                hiddenLessons = hiddenLessons.map { it.toEntity() }.toHashSet()
            )
        }

        fun SettingsEntity.toDto(): SettingsDto {
            return SettingsDto(
                id = id,
                group = group,
                isEnabledNewScheduleNotifications = isEnabledNewScheduleNotifications,
                isEnabledChangedScheduleNotifications = isEnabledChangedScheduleNotifications,
                isEnabledComingLessonsNotifications = isEnabledComingLessonsNotifications,
                hiddenLessons = hiddenLessons.map { it.toDto() }.toHashSet(),
            )
        }

        fun Iterable<UserEntity>.getGroupedEntityBySettingsUsers() =
            this
                .groupBy { "${it.settings.group} ${it.settings.hiddenLessons}" }

        fun Iterable<UserDto>.getGroupedBySettingsUsers() =
            this
                .groupBy { "${it.settings.group} ${it.settings.hiddenLessons}" }
    }
}