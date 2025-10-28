package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toScheduleType
import com.melowetty.hsepermhelper.messaging.event.notification.timetable.TimetableAddedNotification
import com.melowetty.hsepermhelper.repository.UserRepository
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.timetable.TimetableInfoEncoder
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class TimetableNotificationService(
    private val notificationService: NotificationService,
    private val userRepository: UserRepository,
) {
    fun notifyAboutAddedTimetables(timetables: List<InternalTimetableInfo>) {
        val notifications = timetables.groupBy { it.educationType }.map { group ->
            val groupedInfo = group.value.map {
                ScheduleInfo(
                    id = TimetableInfoEncoder.encode(it.id, it.source),
                    number = it.number,
                    start = it.start,
                    end = it.end,
                    scheduleType = it.type.toScheduleType(),
                )
            }

            Pair(group.key, groupedInfo)
        }

        //userRepository.
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}