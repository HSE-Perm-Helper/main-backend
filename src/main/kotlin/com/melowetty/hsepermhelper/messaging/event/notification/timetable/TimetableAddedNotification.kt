package com.melowetty.hsepermhelper.messaging.event.notification.timetable

import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.messaging.event.notification.NotificationV2
import com.melowetty.hsepermhelper.messaging.event.notification.NotificationType

class TimetableAddedNotification(
    val timetablesInfo: List<ScheduleInfo>
) : NotificationV2(NotificationType.TIMETABLE_ADDED)