package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.getGroupedEntityBySettingsUsers
import com.melowetty.hsepermhelper.messaging.event.notification.schedule.UpcomingLessonsNotification
import com.melowetty.hsepermhelper.persistence.repository.UserRepository
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.timetable.PersonalTimetableService
import com.melowetty.hsepermhelper.service.user.UserHiddenLessonService
import com.melowetty.hsepermhelper.util.DateUtils
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NotifyComingScheduleJob(
    private val userRepository: UserRepository,
    private val timetableService: PersonalTimetableService,
    private val notificationService: NotificationService,
    private val hiddenLessonService: UserHiddenLessonService,
) {
    @Deprecated("Iterate over all users via user query service")
    @Scheduled(cron = "0 0 19 * * 0-5", zone = DateUtils.PERM_TIME_ZONE_STR)
    fun notifyComingLessons() {
        userRepository.findAllByIsEnabledComingLessonsNotifications(true)
            .let {
                val hiddenLessons = hiddenLessonService.getUsersHiddenLessons(it.map { it.id })
                it.getGroupedEntityBySettingsUsers(hiddenLessons)
            }
            .forEach { (_, users) ->
                if (users.isEmpty()) return@forEach
                val lessons = timetableService.getTomorrowLessons(users.first().id) ?: return@forEach
                if (lessons.isEmpty()) return@forEach

                val notification = UpcomingLessonsNotification(
                    lessons = lessons,
                    users = users.map { it.telegramId }
                )

                notificationService.sendNotification(notification)
            }
    }
}