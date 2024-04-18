package com.melowetty.hsepermhelper.notification

import com.melowetty.hsepermhelper.models.ScheduleInfo
import io.swagger.v3.oas.annotations.media.Schema

data class ScheduleAddedNotification(
    @Schema(description = "Расписание, которое было добавлено")
    val targetSchedule: ScheduleInfo,
    @Schema(description = "Список Telegram ID, которым требуется выслать оповещение о новом расписании")
    val users: List<Long>,
): Notification() {
    override fun getEventType(): String {
        return "SCHEDULE_ADDED_EVENT"
    }

    override fun getNotificationType(): String {
        return "SCHEDULE_ADDED"
    }

    override fun hashCode(): Int {
        return super.hashCode() + targetSchedule.hashCode() * 31 + users.hashCode() * 31
    }

    override fun toV2(): Notification {
        return ScheduleAddedNotificationV2(
            targetSchedule = targetSchedule.toScheduleInfoV2(),
            users = users,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleAddedNotification

        if (targetSchedule != other.targetSchedule) return false
        if (users != other.users) return false

        return true
    }
}