package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.domain.dto.ApiUserHideLesson
import com.melowetty.hsepermhelper.domain.dto.SettingsDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.timetable.EducationType
import com.melowetty.hsepermhelper.persistence.entity.EducationGroupEntity
import com.melowetty.hsepermhelper.persistence.entity.UserEntity
import com.melowetty.hsepermhelper.persistence.projection.HideLessonRecord
import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import java.util.UUID

// TODO: make as object
class UserExtensions {
    companion object {
        fun UserEntity.toDto(hiddenLessons: List<HideLessonRecord>): UserDto {
            return UserDto(
                id = id(),
                telegramId = telegramId,
                settings = SettingsDto(
                    group = educationGroup.group,
                    hiddenLessons = hiddenLessons.map { it.toApiDto() },
                    isEnabledNewScheduleNotifications = isEnabledNewScheduleNotifications,
                    isEnabledChangedScheduleNotifications = isEnabledChangedScheduleNotifications,
                    isEnabledComingLessonsNotifications = isEnabledComingLessonsNotifications,
                ),
                createdDate = createdDate,
                email = email,
                roles = roles
            )
        }

        fun UserRecord.toDto(): UserDto {
            return UserDto(
                id = id,
                telegramId = telegramId,
                settings = SettingsDto(
                    group = educationGroup.group,
                    hiddenLessons = hiddenLessons.map { it.toApiDto() },
                    isEnabledNewScheduleNotifications = isEnabledNewScheduleNotifications,
                    isEnabledChangedScheduleNotifications = isEnabledChangedScheduleNotifications,
                    isEnabledComingLessonsNotifications = isEnabledComingLessonsNotifications,
                ),
                createdDate = createdDate,
                email = email,
                roles = roles
            )
        }

        fun UserDto.toEntity(): UserEntity {
            return UserEntity(
                id = id,
                telegramId = telegramId,
                educationGroup = EducationGroupEntity(
                    settings.group,
                    EducationType.BACHELOR_OFFLINE,
                ),
                isEnabledNewScheduleNotifications = settings.isEnabledNewScheduleNotifications,
                isEnabledChangedScheduleNotifications = settings.isEnabledChangedScheduleNotifications,
                isEnabledComingLessonsNotifications = settings.isEnabledComingLessonsNotifications,
                createdDate = createdDate,
                email = email,
                roles = roles,
            )
        }

        fun HideLessonRecord.toApiDto(): ApiUserHideLesson {
            return ApiUserHideLesson(
                lesson = lesson,
                lessonType = lessonType,
                subGroup = subGroup,
            )
        }

        fun Iterable<UserEntity>.getGroupedEntityBySettingsUsers(hiddenLessons: Map<UUID, List<HideLessonRecord>>) =
            this
                .groupBy { "${it.educationGroup.group}${hiddenLessons.getOrDefault(it.id, listOf()).hashCode()}" }

        fun Iterable<UserDto>.getGroupedBySettingsUsers() =
            this
                .groupBy { "${it.settings.group} ${it.settings.hiddenLessons}" }
    }
}