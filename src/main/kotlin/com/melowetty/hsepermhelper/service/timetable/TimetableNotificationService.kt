package com.melowetty.hsepermhelper.service.timetable

import com.melowetty.hsepermhelper.domain.model.Feature
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toScheduleType
import com.melowetty.hsepermhelper.messaging.event.notification.timetable.TimetableAddedNotification
import com.melowetty.hsepermhelper.messaging.event.notification.timetable.TimetableChangedNotification
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import com.melowetty.hsepermhelper.service.FeatureManager
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.timetable.TimetableInfoEncoder
import com.melowetty.hsepermhelper.timetable.model.EducationType
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.util.Paginator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.util.*

@Service
class TimetableNotificationService(
    private val notificationService: NotificationService,
    private val userStorage: UserStorage,
    private val featuresManager: FeatureManager,
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

    fun notifyAboutChangedTimetable(
        timetableInfo: InternalTimetableInfo,
        changedDays: List<DayOfWeek>,
        receivers: List<UUID>,
    ) {
        val users = userStorage.getUsersById(receivers)
            .filter { featuresManager.isEnabledForUser(Feature.NEW_CHANGED_TIMETABLE_NOTIFICATION, it) }

        if (users.isEmpty()) return

        val dto = ScheduleInfo(
            id = TimetableInfoEncoder.encode(timetableInfo.id, timetableInfo.source),
            number = timetableInfo.number,
            start = timetableInfo.start,
            end = timetableInfo.end,
            scheduleType = timetableInfo.type.toScheduleType(),
        )

        val notification = TimetableChangedNotification(
            timetableInfo = dto,
            changedDays = changedDays,
        )

        notificationService.sendBatchUserNotification(users.map { it.id }, notification)
    }

    private fun processAddedGroupTimetables(educationType: EducationType, group: List<ScheduleInfo>) {
        Paginator.fetchPageable(
            fetchFunction = { limit, token ->
                userStorage.findUsersAfterId(
                    lastId = token,
                    size = limit,
                    educationType = educationType,
                    isEnabledNewSchedule = true,
                    options = UserStorage.Options(
                        withRoles = true
                    )
                )
            }
        ) {
            val ids = it.filter {
                featuresManager.isEnabledForUser(Feature.NEW_ADDED_TIMETABLES_NOTIFICATION, it)
            }.map { user ->
                user.id
            }

            if (ids.isEmpty()) return@fetchPageable

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