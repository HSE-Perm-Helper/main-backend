package com.melowetty.hsepermhelper.notification.timetable

import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.notification.NotificationV2
import com.melowetty.hsepermhelper.notification.NotificationType

class TimetableAddedNotification(
    val timetablesInfo: List<ScheduleInfo>
) : NotificationV2(NotificationType.TIMETABLE_ADDED)