package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.persistence.entity.UserEntity
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.getGroupedEntityBySettingsUsers
import com.melowetty.hsepermhelper.messaging.event.notification.schedule.UpcomingLessonsNotification
import com.melowetty.hsepermhelper.persistence.repository.UserRepository
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.OldPersonalScheduleService
import com.melowetty.hsepermhelper.service.UserHiddenLessonService
import com.melowetty.hsepermhelper.util.DateUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils
import java.time.LocalDate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NotifyComingScheduleJob(
    private val userRepository: UserRepository,
    private val scheduleService: OldPersonalScheduleService,
    private val notificationService: NotificationService,
    private val hiddenLessonService: UserHiddenLessonService,
) {
    @Deprecated("Iterate over all users via user query service")
    @Scheduled(cron = "0 0 19 * * 0-5", zone = DateUtils.PERM_TIME_ZONE_STR)
    fun notifyComingLessons() {
        val currentDate = LocalDate.now().plusDays(1)
        userRepository.findAllByIsEnabledComingLessonsNotifications(true)
            .let {
                val hiddenLessons = hiddenLessonService.getUsersHiddenLessons(it.map { it.id })
                it.getGroupedEntityBySettingsUsers(hiddenLessons)
            }
            .forEach { (_, users) ->
                if (users.isEmpty()) return@forEach
                val upcomingSchedule = getUpcomingSchedule(currentDate, users.first()) ?: return@forEach
                if (upcomingSchedule.lessons.isEmpty()) return@forEach

                val notification = UpcomingLessonsNotification(
                    targetSchedule = upcomingSchedule,
                    users = users.map { it.telegramId }
                )

                notificationService.sendNotification(notification)
            }
    }

    private fun getCurrentSchedule(currentDate: LocalDate, user: UserEntity): Schedule? {
        val schedules = scheduleService.getUserSchedulesByTelegramId(user.telegramId)
        return ScheduleUtils.getWeekScheduleByDate(schedules, currentDate)
    }

    private fun getUpcomingSchedule(currentDate: LocalDate, user: UserEntity): Schedule? {
        val schedule = getCurrentSchedule(currentDate, user) ?: return null
        val upcomingLessons = ScheduleUtils.getLessonsAtDateInWeekSchedule(schedule, currentDate)

        return schedule.copy(
            lessons = upcomingLessons
        )
    }
}