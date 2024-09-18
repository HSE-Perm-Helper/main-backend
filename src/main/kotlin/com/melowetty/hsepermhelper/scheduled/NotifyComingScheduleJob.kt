package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.annotation.Slf4j
import com.melowetty.hsepermhelper.domain.entity.UserEntity
import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleType
import com.melowetty.hsepermhelper.model.ScheduledTime
import com.melowetty.hsepermhelper.notification.UpcomingLessonsNotification
import com.melowetty.hsepermhelper.repository.UserRepository
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.ScheduleService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Slf4j
class NotifyComingScheduleJob(
    private val userRepository: UserRepository,
    private val scheduleService: ScheduleService,
    private val notificationService: NotificationService
) {
    @Scheduled(cron = "0 0 19 * * 0-5", zone = "GMT+05:00")
    fun notifyComingLessons() {
        val currentDate = LocalDate.now().plusDays(1)
        getGroupedBySettingsUsers()
            .forEach { (_, users) ->
                if (users.isEmpty()) return@forEach
                val upcomingSchedule = getUpcomingSchedule(currentDate, users.first())

                val notification = UpcomingLessonsNotification(
                    targetSchedule = upcomingSchedule,
                    users = users.map { it.telegramId }
                )

                notificationService.sendNotification(notification)
            }
    }

    private fun getGroupedBySettingsUsers() =
        userRepository.findAllBySettings_IsEnabledComingLessonsNotifications(true)
            .groupBy { "${it.settings.group} ${it.settings.subGroup} ${it.settings.includeCommonEnglish} ${it.settings.includeCommonMinor}" }

    private fun getCurrentSchedule(currentDate: LocalDate, user: UserEntity): Schedule {
        val schedules = scheduleService.getUserSchedulesById(id = user.id)
        return schedules.filter { it.scheduleType == ScheduleType.WEEK_SCHEDULE }.first {
            (it.start.isBefore(currentDate).or(it.start.isEqual(currentDate)))
                    && (it.end.isAfter(currentDate).or(it.end.isEqual(currentDate)))
        }
    }

    private fun getUpcomingSchedule(currentDate: LocalDate, user: UserEntity): Schedule {
        val schedule = getCurrentSchedule(currentDate, user)
        val upcomingLessons = schedule.lessons.filter {
            (it.time as ScheduledTime).date.isEqual(currentDate)
        }

        return schedule.copy(
            lessons = upcomingLessons
        )
    }
}