package com.melowetty.hsepermhelper.notification

import com.melowetty.hsepermhelper.model.Schedule

data class UpcomingLessonsNotification(
    val targetSchedule: Schedule,
    val users: List<Long>,
) : Notification() {
    override fun getNotificationType(): String {
        return "UPCOMING_LESSONS"
    }
}
