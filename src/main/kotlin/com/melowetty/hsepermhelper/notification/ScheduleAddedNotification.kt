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
        return super.hashCode() + targetSchedule.hashCode() * 16
    }
}