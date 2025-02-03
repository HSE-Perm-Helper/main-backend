package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toSchedule
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toScheduleInfo
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.getGroupedBySettingsUsers
import com.melowetty.hsepermhelper.model.event.ExcelSchedulesChanging
import com.melowetty.hsepermhelper.model.excel.ExcelLesson
import com.melowetty.hsepermhelper.model.excel.ExcelSchedule
import com.melowetty.hsepermhelper.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.model.lesson.LessonType
import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.notification.ScheduleAddedNotification
import com.melowetty.hsepermhelper.notification.ScheduleChangedForUserNotification
import com.melowetty.hsepermhelper.repository.ExcelScheduleRepository
import com.melowetty.hsepermhelper.util.ScheduleUtils
import java.time.LocalDate
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class ExcelScheduleService(
    private val scheduleRepository: ExcelScheduleRepository,
    private val userService: UserService,
    private val notificationService: NotificationService,
) {
    private fun filterSchedules(
        schedules: List<ExcelSchedule>,
        user: UserDto,
        withoutHiddenLessons: Boolean = true
    ): List<ExcelSchedule> {
        val filteredSchedules = schedules.map { schedule ->
            filterSchedule(schedule, user, withoutHiddenLessons)
        }
        return filteredSchedules
    }

    fun filterSchedule(schedule: ExcelSchedule, user: UserDto, withoutHiddenLessons: Boolean = true): ExcelSchedule {
        val filteredLessons = schedule.lessons.filter { lesson: ExcelLesson ->
            lesson.group == user.settings.group
        }.filter {
            (it.lessonType == LessonType.COMMON_ENGLISH).not()
        }.filter {
            if (withoutHiddenLessons) {
                return@filter user.settings.hiddenLessons.any { hideLessonEntity ->
                    hideLessonEntity.lesson == it.subject
                            && hideLessonEntity.lessonType == it.lessonType
                            && hideLessonEntity.subGroup == it.subGroup

                }.not()
            }
            return@filter true
        }

        return schedule.copy(
            lessons = filteredLessons
        )
    }

    fun getUserSchedules(user: UserDto): List<Schedule> {
        return filterSchedules(scheduleRepository.getSchedules(), user).map { it.toSchedule() }
    }

    fun getAvailableSchedules(): List<ScheduleInfo> {
        val currentDate = LocalDate.now()
        return scheduleRepository.getSchedules().filter { it.end.isAfter(currentDate) }.map { it.toScheduleInfo() }
    }

    fun getUserSchedule(user: UserDto, start: LocalDate, end: LocalDate): Schedule {
        val schedule = scheduleRepository.getSchedules().filter { it.start == start && end == it.end }.getOrNull(0)
        if (schedule == null) throw ScheduleNotFoundException("Расписание с такими датами не найдено!")
        return filterSchedule(schedule, user).toSchedule()
    }

    fun getAvailableCourses(): List<Int> {
        return scheduleRepository.getAvailableCourses()
    }

    fun getAvailablePrograms(course: Int): List<String> {
        return scheduleRepository.getAvailablePrograms(course = course)
    }

    fun getAvailableGroups(course: Int, program: String): List<String> {
        return scheduleRepository.getAvailableGroups(course = course, program = program)
    }

    fun getAvailableLessonsForHiding(user: UserDto): List<AvailableLessonForHiding> {
        val schedules = filterSchedules(scheduleRepository.getSchedules(), user, withoutHiddenLessons = false)

        val blacklistTypes =
            setOf(LessonType.COMMON_ENGLISH, LessonType.COMMON_MINOR, LessonType.ENGLISH, LessonType.MINOR)

        return schedules.asSequence().map { it.lessons }.flatten().map {
            AvailableLessonForHiding(lesson = it.subject, lessonType = it.lessonType, subGroup = it.subGroup)
        }.filter {
            blacklistTypes.contains(it.lessonType).not()
        }.distinct().toList()
    }

    @EventListener
    fun handleScheduleChanging(event: ExcelSchedulesChanging) {
        val editedSchedules = event.changed
        val addedSchedules = event.added

        addedSchedules.forEach { schedule ->
            processNewExcelSchedule(schedule)
        }

        editedSchedules.forEach outerFor@{ schedule ->
            processEditedExcelSchedule(schedule.before, schedule.after)
        }
    }

    fun processNewExcelSchedule(schedule: ExcelSchedule) {
        val users = mutableListOf<Long>()
        users.addAll(userService.getAllUsers()
            .filter { it.settings.isEnabledNewScheduleNotifications }
            .map { it.telegramId })
        val scheduleAddedNotification = ScheduleAddedNotification(
            targetSchedule = schedule.toScheduleInfo(),
            users = users,
        )
        notificationService.sendNotification(scheduleAddedNotification)
    }

    fun processEditedExcelSchedule(before: ExcelSchedule, after: ExcelSchedule) {
        userService.getAllUsers()
            .filter { user ->
                user.settings.isEnabledChangedScheduleNotifications
            }
            .getGroupedBySettingsUsers().forEach { (_, groupedUsers) ->
                val user = groupedUsers.firstOrNull() ?: return@forEach
                val before = filterSchedule(before, user)
                val after = filterSchedule(after, user)
                if (before.lessons.toHashSet() != after.lessons.toHashSet()) {
                    val scheduleChangedEvent = ScheduleChangedForUserNotification(
                        targetSchedule = after.toScheduleInfo(),
                        users = groupedUsers.map { it.telegramId },
                        differentDays = ScheduleUtils.getDifferentDaysByLessons(before, after)
                    )
                    notificationService.sendNotification(scheduleChangedEvent)
                }
            }
    }
}