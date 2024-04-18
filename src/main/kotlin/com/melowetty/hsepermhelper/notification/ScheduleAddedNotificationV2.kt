package com.melowetty.hsepermhelper.notification

import com.melowetty.hsepermhelper.models.ScheduleInfoV2
import io.swagger.v3.oas.annotations.media.Schema

data class ScheduleAddedNotificationV2(
    @Schema(description = "Расписание, которое было добавлено")
    val targetSchedule: ScheduleInfoV2,
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
        TODO("Not yet implemented")
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleAddedNotificationV2

        if (targetSchedule != other.targetSchedule) return false
        if (users != other.users) return false

        return true
    }
}