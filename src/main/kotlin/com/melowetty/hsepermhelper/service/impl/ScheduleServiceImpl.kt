package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toScheduleInfo
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.getGroupedBySettingsUsers
import com.melowetty.hsepermhelper.model.AvailableLessonForHiding
import com.melowetty.hsepermhelper.model.Lesson
import com.melowetty.hsepermhelper.model.LessonType
import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleInfo
import com.melowetty.hsepermhelper.model.ScheduleType
import com.melowetty.hsepermhelper.model.SchedulesChanging
import com.melowetty.hsepermhelper.notification.ScheduleAddedNotification
import com.melowetty.hsepermhelper.notification.ScheduleChangedForUserNotification
import com.melowetty.hsepermhelper.repository.ScheduleRepository
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.ScheduleService
import com.melowetty.hsepermhelper.service.UserService
import com.melowetty.hsepermhelper.util.DateUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils.Companion.filterWeekSchedules
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

@Service
class ScheduleServiceImpl(
    private val scheduleRepository: ScheduleRepository,
    private val userService: UserService,
    private val notificationService: NotificationService
) : ScheduleService {
    private fun filterSchedules(schedules: List<Schedule>, user: UserDto): List<Schedule> {
        val filteredSchedules = schedules.map { schedule ->
            filterSchedule(schedule, user)
        }
        return filteredSchedules
    }

    fun filterSchedule(schedule: Schedule, user: UserDto): Schedule {
        val course = ScheduleUtils.getCourseFromGroup(user.settings.group) // todo TEMP FIX
        if (course == 3 || course == 4 || ScheduleUtils.getShortGroupFromGroup(user.settings.group) == "ИЯ") {
            return tempFilterSchedule(schedule, user)
        }

        val filteredLessons = schedule.lessons.filter { lesson: Lesson ->
            if (lesson.subGroup != null) lesson.group == user.settings.group
                    && lesson.subGroup == user.settings.subGroup
            else lesson.group == user.settings.group
        }.filter {
            (it.lessonType == LessonType.COMMON_ENGLISH).not()
        }.filter {
            user.settings.hiddenLessons.any { hideLessonEntity ->
                hideLessonEntity.lesson == it.subject
                        && hideLessonEntity.lessonType == it.lessonType
                        && hideLessonEntity.subGroup == it.subGroup

            }.not()
        }

        return schedule.copy(
            lessons = filteredLessons
        )
    }

    private fun tempFilterSchedule(schedule: Schedule, user: UserDto): Schedule {
        val filteredLessons = schedule.lessons.filter { lesson: Lesson ->
            lesson.group == user.settings.group
        }.filter {
            (it.lessonType == LessonType.COMMON_ENGLISH).not()
        }.filter {
            user.settings.hiddenLessons.any { hideLessonEntity ->
                hideLessonEntity.lesson == it.subject
                        && hideLessonEntity.lessonType == it.lessonType
                        && hideLessonEntity.subGroup == it.subGroup

            }.not()
        }

        return schedule.copy(
            lessons = filteredLessons.map {
                if (it.subGroup == null) it
                else it.copy(subject = "${it.subject} <b>(${it.subGroup} подгруппа)</b>")
            }
        )
    }

    override fun getUserSchedulesByTelegramId(telegramId: Long): List<Schedule> {
        val user = userService.getByTelegramId(telegramId)
        return filterSchedules(scheduleRepository.getSchedules(), user)
    }

    override fun getAvailableSchedules(): List<ScheduleInfo> {
        val currentDate = LocalDate.now()
        return scheduleRepository.getSchedules().filter { it.end.isAfter(currentDate) }.map { it.toScheduleInfo() }
    }

    override fun getUserScheduleByTelegramId(telegramId: Long, start: LocalDate, end: LocalDate): Schedule {
        val schedule = scheduleRepository.getSchedules().filter { it.start == start && end == it.end }.getOrNull(0)
        if (schedule == null) throw ScheduleNotFoundException("Расписание с такими датами не найдено!")
        val user = userService.getByTelegramId(telegramId)
        return filterSchedule(schedule, user)
    }

    override fun getUserSchedulesById(id: UUID): List<Schedule> {
        val user = userService.getById(id)
        return filterSchedules(scheduleRepository.getSchedules(), user)
    }

    @EventListener
    fun handleScheduleChanging(event: SchedulesChanging) {
        val editedSchedules = event.changed
        val addedSchedules = event.added
        addedSchedules.forEach { schedule ->
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
        editedSchedules.forEach outerFor@{ schedule ->
            val users = mutableSetOf<Long>()
            userService.getAllUsers()
                .filter { user ->
                    user.settings.isEnabledChangedScheduleNotifications
                }
                .getGroupedBySettingsUsers().forEach { (_, groupedUsers) ->
                    val user = groupedUsers.firstOrNull() ?: return@forEach
                    val before = filterSchedule(schedule.before, user)
                    val after = filterSchedule(schedule.after, user)
                    if (before.lessons.toHashSet() != after.lessons.toHashSet()) {
                        users.addAll(groupedUsers.map { it.telegramId })
                    }
                }
            if (users.isNotEmpty()) {
                val scheduleChangedEvent = ScheduleChangedForUserNotification(
                    targetSchedule = schedule.after.toScheduleInfo(),
                    users = users.toList()
                )
                notificationService.sendNotification(scheduleChangedEvent)
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

    override fun getTodayLessons(telegramId: Long): List<Lesson> {
        val schedules = getUserSchedulesByTelegramId(telegramId)
            .filterWeekSchedules()
        val todayDate = LocalDate.now(DateUtils.PERM_TIME_ZONE.toZoneId())

        val schedule = ScheduleUtils.getWeekScheduleByDate(schedules, todayDate) ?: return listOf()
        return ScheduleUtils.getLessonsAtDateInWeekSchedule(schedule, todayDate)
    }

    override fun getTomorrowLessons(telegramId: Long): List<Lesson> {
        var tomorrowDate = LocalDate.now(DateUtils.PERM_TIME_ZONE.toZoneId()).plusDays(1)
        if (tomorrowDate.dayOfWeek == DayOfWeek.SUNDAY) tomorrowDate = tomorrowDate.plusDays(1)
        val schedules = getUserSchedulesByTelegramId(telegramId)
            .filterWeekSchedules()

        val schedule = ScheduleUtils.getWeekScheduleByDate(schedules, tomorrowDate) ?: return listOf()
        return ScheduleUtils.getLessonsAtDateInWeekSchedule(schedule, tomorrowDate)
    }

    override fun getAvailableLessonsForHiding(telegramId: Long): List<AvailableLessonForHiding> {
        val schedule = getUserSchedulesByTelegramId(telegramId).firstOrNull {
            it.scheduleType == ScheduleType.QUARTER_SCHEDULE
        } ?: throw ScheduleNotFoundException("Расписания на модуль пока нет")

        val blacklistTypes =
            setOf(LessonType.COMMON_ENGLISH, LessonType.COMMON_MINOR, LessonType.ENGLISH, LessonType.MINOR)

        return schedule.lessons.map {
            AvailableLessonForHiding(lesson = it.subject, lessonType = it.lessonType, subGroup = it.subGroup)
        }.filter {
            blacklistTypes.contains(it.lessonType).not()
        }
    }
}