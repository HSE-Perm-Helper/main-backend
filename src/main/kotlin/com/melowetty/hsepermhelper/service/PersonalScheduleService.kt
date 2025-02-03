package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.model.lesson.Lesson
import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.util.DateUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils.Companion.filterWeekSchedules
import java.time.DayOfWeek
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class PersonalScheduleService(
    private val excelScheduleService: ExcelScheduleService,
    private val userService: UserService,
) {
    fun getUserSchedulesByTelegramId(telegramId: Long): List<Schedule> {
        val user = userService.getByTelegramId(telegramId)
        return excelScheduleService.getUserSchedules(user)
    }

   fun getAvailableSchedules(): List<ScheduleInfo> {
        return excelScheduleService.getAvailableSchedules()
    }

    fun getUserScheduleByTelegramId(telegramId: Long, start: LocalDate, end: LocalDate): Schedule {
        val user = userService.getByTelegramId(telegramId)
        return excelScheduleService.getUserSchedule(user, start, end)
    }

    fun getTodayLessons(telegramId: Long): List<Lesson> {
        val schedules = getUserSchedulesByTelegramId(telegramId)
            .filterWeekSchedules()
        val todayDate = LocalDate.now(DateUtils.PERM_TIME_ZONE.toZoneId())

        val schedule = ScheduleUtils.getWeekScheduleByDate(schedules, todayDate) ?: return listOf()
        return ScheduleUtils.getLessonsAtDateInWeekSchedule(schedule, todayDate)
    }

    fun getTomorrowLessons(telegramId: Long): List<Lesson> {
        var tomorrowDate = LocalDate.now(DateUtils.PERM_TIME_ZONE.toZoneId()).plusDays(1)
        if (tomorrowDate.dayOfWeek == DayOfWeek.SUNDAY) tomorrowDate = tomorrowDate.plusDays(1)
        val schedules = getUserSchedulesByTelegramId(telegramId)
            .filterWeekSchedules()

        val schedule = ScheduleUtils.getWeekScheduleByDate(schedules, tomorrowDate) ?: return listOf()
        return ScheduleUtils.getLessonsAtDateInWeekSchedule(schedule, tomorrowDate)
    }

    fun getAvailableCourses(): List<Int> {
        return excelScheduleService.getAvailableCourses()
    }

    fun getAvailablePrograms(course: Int): List<String> {
        return excelScheduleService.getAvailablePrograms(course = course)
    }

    fun getAvailableGroups(course: Int, program: String): List<String> {
        return excelScheduleService.getAvailableGroups(course = course, program = program)
    }

    fun getAvailableLessonsForHiding(telegramId: Long): List<AvailableLessonForHiding> {
        val user = userService.getByTelegramId(telegramId)
        return excelScheduleService.getAvailableLessonsForHiding(user)
    }
}