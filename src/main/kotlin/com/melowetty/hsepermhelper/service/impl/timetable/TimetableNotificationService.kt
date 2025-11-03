package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toScheduleType
import com.melowetty.hsepermhelper.messaging.event.notification.timetable.TimetableAddedNotification
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.UserQueryService
import com.melowetty.hsepermhelper.timetable.TimetableInfoEncoder
import com.melowetty.hsepermhelper.timetable.model.EducationType
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.util.Paginator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class TimetableNotificationService(
    private val notificationService: NotificationService,
    private val userQueryService: UserQueryService,
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

        notifications.forEach { (educationType, group) -> processAddedGroupTimetables(educationType, group) }
    }

    private fun processAddedGroupTimetables(educationType: EducationType, group: List<ScheduleInfo>) {
        Paginator.fetchPageable(
            fetchFunction = { limit, token ->
                userQueryService.findUsersAfterId(
                    lastId = token,
                    size = limit,
                    educationType = educationType,
                    isEnabledNewSchedule = true,
                )
            }
        ) {
            val ids = it.map { user ->
                user.id
            }

            val notification = TimetableAddedNotification(
                timetablesInfo = group
            )

            logger.info { "Sending batch added timetables notification for ${ids.size} users" }

            notificationService.sendBatchUserNotification(ids, notification)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}