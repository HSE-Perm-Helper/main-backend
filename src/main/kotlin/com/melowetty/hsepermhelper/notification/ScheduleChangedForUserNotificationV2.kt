package com.melowetty.hsepermhelper.notification

import com.melowetty.hsepermhelper.models.ScheduleInfoV2
import io.swagger.v3.oas.annotations.media.Schema

data class ScheduleChangedForUserNotificationV2(
    @Schema(description = "Информация о расписании, в котором произошло изменение")
    val targetSchedule: ScheduleInfoV2,
    @Schema(description = "Список Telegram ID, которым требуется выслать оповещение о изменении в расписании")
    val users: List<Long>,
): Notification() {
    override fun getEventType(): String {
        return "SCHEDULE_CHANGED_FOR_USER_EVENT"
    }

    override fun getNotificationType(): String {
        return "SCHEDULE_CHANGED_FOR_USER"
    }

    override fun hashCode(): Int {
        return super.hashCode() + targetSchedule.hashCode() * 32 + users.hashCode() * 31
    }

    override fun toV2(): Notification {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleChangedForUserNotificationV2

        if (targetSchedule != other.targetSchedule) return false
        if (users != other.users) return false

        return true
    }
}