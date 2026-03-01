package com.melowetty.hsepermhelper.persistence.projection

import com.melowetty.hsepermhelper.domain.model.user.UserRole
import com.melowetty.hsepermhelper.persistence.entity.UserEntity
import java.time.LocalDateTime
import java.util.*

data class UserRecord(
    val id: UUID,
    val telegramId: Long,
    val email: String?,
    val educationGroup: EducationGroupRecord,
    val isEnabledNewScheduleNotifications: Boolean,
    val isEnabledChangedScheduleNotifications: Boolean,
    val isEnabledComingLessonsNotifications: Boolean,
    val createdDate: LocalDateTime,
    val roles: List<UserRole> = emptyList(),
    val hiddenLessons: List<HideLessonRecord> = emptyList(),
) {
    companion object {
        fun from(entity: UserEntity): UserRecord {
            return UserRecord(
                    id = entity.id(),
                    telegramId = entity.telegramId,
                    email = entity.email,
                    educationGroup = EducationGroupRecord.from(entity.educationGroup),
                    isEnabledNewScheduleNotifications = entity.isEnabledNewScheduleNotifications,
                    isEnabledChangedScheduleNotifications = entity.isEnabledChangedScheduleNotifications,
                    isEnabledComingLessonsNotifications = entity.isEnabledComingLessonsNotifications,
                    createdDate = entity.createdDate,
            )
        }
    }
}
