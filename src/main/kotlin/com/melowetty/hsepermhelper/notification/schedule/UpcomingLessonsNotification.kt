package com.melowetty.hsepermhelper.notification.schedule

import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.notification.Notification

data class UpcomingLessonsNotification(
    val targetSchedule: Schedule,
    val users: List<Long>,
) : Notification() {
    override fun getNotificationType(): String {
        return "UPCOMING_LESSONS"
    }
}
