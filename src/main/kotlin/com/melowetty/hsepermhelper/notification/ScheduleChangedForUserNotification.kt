package com.melowetty.hsepermhelper.notification

import com.melowetty.hsepermhelper.model.ScheduleInfo
import io.swagger.v3.oas.annotations.media.Schema
import java.time.DayOfWeek

data class ScheduleChangedForUserNotification(
    @Schema(description = "Информация о расписании, в котором произошло изменение")
    val targetSchedule: ScheduleInfo,
    val differentDays: List<DayOfWeek>,
    @Schema(description = "Список Telegram ID, которым требуется выслать оповещение о изменении в расписании")
    val users: List<Long>,
) : Notification() {
    override fun getNotificationType(): String {
        return "SCHEDULE_CHANGED_FOR_USER"
    }
}