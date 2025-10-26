package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class TimetableNotificationService(
    private val notificationService: NotificationService,
) {
    fun notifyAboutAddedTimetables(timetables: List<InternalTimetableInfo>) {
        logger.info { "Sending notification about added timetables" }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}