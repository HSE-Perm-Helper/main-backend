package com.melowetty.hsepermhelper.notification

import com.melowetty.hsepermhelper.models.ScheduleInfo
import io.swagger.v3.oas.annotations.media.Schema

data class ScheduleChangedForUserNotification(
    @Schema(description = "Информация о расписании, в котором произошло изменение")
    val targetSchedule: ScheduleInfo,
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
        return ScheduleChangedForUserNotificationV2(
            targetSchedule = targetSchedule.toScheduleInfoV2(),
            users = users,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleChangedForUserNotification

        if (targetSchedule != other.targetSchedule) return false
        if (users != other.users) return false

        return true
    }
}