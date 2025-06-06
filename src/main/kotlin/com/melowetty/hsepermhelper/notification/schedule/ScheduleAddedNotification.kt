package com.melowetty.hsepermhelper.notification.schedule

import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.notification.Notification
import io.swagger.v3.oas.annotations.media.Schema

data class ScheduleAddedNotification(
    @Schema(description = "Расписание, которое было добавлено")
    val targetSchedule: ScheduleInfo,
    @Schema(description = "Список Telegram ID, которым требуется выслать оповещение о новом расписании")
    val users: List<Long>,
) : Notification() {
    override fun getNotificationType(): String {
        return "SCHEDULE_ADDED"
    }
}