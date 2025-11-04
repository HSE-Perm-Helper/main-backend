package com.melowetty.hsepermhelper.messaging.event.notification.timetable

import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.messaging.event.notification.NotificationType
import com.melowetty.hsepermhelper.messaging.event.notification.NotificationV2
import java.time.DayOfWeek

class TimetableChangedNotification(
    val timetableInfo: ScheduleInfo,
    val changedDays: List<DayOfWeek>,
) : NotificationV2(NotificationType.TIMETABLE_CHANGED) {
    override fun toString(): String {
        return "TimetableChangedNotification(timetable={${timetableInfo.id})"
    }
}