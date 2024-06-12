package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.dto.UserDto
import com.melowetty.hsepermhelper.event.EventType
import com.melowetty.hsepermhelper.event.ScheduleChangedEvent
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.model.Lesson
import com.melowetty.hsepermhelper.model.LessonType
import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleInfo
import com.melowetty.hsepermhelper.notification.ScheduleAddedNotification
import com.melowetty.hsepermhelper.notification.ScheduleChangedForUserNotification
import com.melowetty.hsepermhelper.repository.ScheduleRepository
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.ScheduleService
import com.melowetty.hsepermhelper.service.UserService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class ScheduleServiceImpl(
    private val scheduleRepository: ScheduleRepository,
    private val userService: UserService,
    private val notificationService: NotificationService
): ScheduleService {
    private fun filterSchedules(schedules: List<Schedule>, user: UserDto): List<Schedule> {
        val filteredSchedules = schedules.map { schedule ->
                filterSchedule(schedule, user)
        }
        return filteredSchedules
    }

    private fun filterSchedule(schedule: Schedule, user: UserDto): Schedule {
        val filteredLessons = schedule.lessons.filter { lesson: Lesson ->
            if (lesson.subGroup != null) lesson.group == user.settings.group
                    && lesson.subGroup == user.settings.subGroup
            else lesson.group == user.settings.group
        }.filter {
            if (it.lessonType != LessonType.COMMON_ENGLISH) true
            else user.settings.includeCommonEnglish
        }.filter {
            if (it.lessonType != LessonType.COMMON_MINOR) true
            else user.settings.includeCommonMinor
        }
        return schedule.copy(
            lessons = filteredLessons
        )
    }

    override fun getUserSchedulesByTelegramId(telegramId: Long): List<Schedule> {
        val user = userService.getByTelegramId(telegramId)
        return filterSchedules(scheduleRepository.getSchedules(), user)
    }

    override fun getAvailableSchedules(): List<ScheduleInfo> {
        return scheduleRepository.getSchedules().map { it.toScheduleInfo() }
    }

    override fun getUserScheduleByTelegramId(telegramId: Long, start: LocalDate, end: LocalDate): Schedule {
        val schedule = scheduleRepository.getSchedules().filter { it.start == start && end == it.end }.getOrNull(0)
        if(schedule == null) throw ScheduleNotFoundException("Расписание с такими датами не найдено!")
        val user = userService.getByTelegramId(telegramId)
        return filterSchedule(schedule, user)
    }

    override fun getUserSchedulesById(id: UUID): List<Schedule> {
        val user = userService.getById(id)
        return filterSchedules(scheduleRepository.getSchedules(), user)
    }

    @EventListener
    fun handleScheduleChanging(event: ScheduleChangedEvent) {
        val editedSchedules = event.changes.getOrDefault(EventType.EDITED, null)
        val addedSchedules = event.changes.getOrDefault(EventType.ADDED, null)
        addedSchedules?.forEach { addedSchedule ->
            if(addedSchedule.after != null) {
                val schedule = addedSchedule.after
                val users = mutableListOf<Long>()
                users.addAll(userService.getAllUsers()
                    .filter { it.settings.isEnabledNewScheduleNotifications }
                    .map { it.telegramId })
                val scheduleAddedNotification = ScheduleAddedNotification(
                    targetSchedule = schedule.toScheduleInfo(),
                    users = users,
                )
                notificationService.addNotification(scheduleAddedNotification)
            }
        }
        editedSchedules?.forEach {
            if (it.before != null && it.after != null) {
                val users = mutableSetOf<Long>()
                userService.getAllUsers()
                    .filter { user ->
                        user.settings.isEnabledChangedScheduleNotifications
                    }
                    .distinctBy { "${it.settings.group} ${it.settings.subGroup}" }.forEach { user ->
                        val before = filterSchedule(it.before, user)
                        val after = filterSchedule(it.after, user)
                        if (before.lessons.toHashSet() != after.lessons.toHashSet()) {
                            users.addAll(userService.getAllUsers().filter {
                                it.settings.group == user.settings.group
                                    && it.settings.subGroup == user.settings.subGroup }
                                .map { it.telegramId })
                        }
                    }
                if (users.isNotEmpty()) {
                    val scheduleChangedEvent = ScheduleChangedForUserNotification(
                        targetSchedule = it.after.toScheduleInfo(),
                        users = users.toList()
                    )
                    notificationService.addNotification(scheduleChangedEvent)
                }
            }
        }
    }

    override fun getAvailableCourses(): List<Int> {
        return scheduleRepository.getAvailableCourses()
    }

    override fun getAvailablePrograms(course: Int): List<String> {
        return scheduleRepository.getAvailablePrograms(course = course)
    }

    override fun getAvailableGroups(course: Int, program: String): List<String> {
        return scheduleRepository.getAvailableGroups(course = course, program = program)
    }

    override fun getAvailableSubgroups(course: Int, program: String, group: String): List<Int> {
        return scheduleRepository.getAvailableSubgroups(course = course, program = program, group = group)
    }
}