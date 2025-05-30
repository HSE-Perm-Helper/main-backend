package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.annotation.Slf4j
import com.melowetty.hsepermhelper.domain.entity.UserEntity
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.getGroupedEntityBySettingsUsers
import com.melowetty.hsepermhelper.notification.schedule.UpcomingLessonsNotification
import com.melowetty.hsepermhelper.repository.UserRepository
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.PersonalScheduleService
import com.melowetty.hsepermhelper.util.DateUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils
import java.time.LocalDate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Slf4j
class NotifyComingScheduleJob(
    private val userRepository: UserRepository,
    private val scheduleService: PersonalScheduleService,
    private val notificationService: NotificationService
) {
    @Scheduled(cron = "0 0 19 * * 0-5", zone = DateUtils.PERM_TIME_ZONE_STR)
    fun notifyComingLessons() {
        val currentDate = LocalDate.now().plusDays(1)
        userRepository.findAllBySettings_IsEnabledComingLessonsNotifications(true)
            .getGroupedEntityBySettingsUsers()
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