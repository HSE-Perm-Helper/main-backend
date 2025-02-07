package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.extension.LessonExtensions.Companion.toLesson
import com.melowetty.hsepermhelper.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.model.lesson.Lesson
import com.melowetty.hsepermhelper.model.lesson.LessonType
import com.melowetty.hsepermhelper.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.util.DateUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils.Companion.filterWeekSchedules
import java.time.DayOfWeek
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class PersonalScheduleService(
    private val excelScheduleService: ExcelScheduleService,
    private val hseAppApiService: HseAppApiService,
    private val userService: UserService,
) {
    private fun getHseAppMinorLessonsByUser(studentEmail: String, dayOfWeek: DayOfWeek,
                                            from: LocalDate, to: LocalDate): List<Lesson> {
        return hseAppApiService.getLessons(studentEmail, from, to)
            .filter { it.dateStart.dayOfWeek == dayOfWeek }
            .map { it.toLesson() }
    }

    fun getUserSchedulesByTelegramId(telegramId: Long): List<Schedule> {
        val user = userService.getByTelegramId(telegramId)

        val schedules = excelScheduleService.getUserSchedules(user)

        return addMinorLessons(user, schedules)
    }

    fun getSchedulesDateRange(schedules: List<Schedule>): Pair<LocalDate, LocalDate>? {
        val weekSchedules = schedules.filterWeekSchedules()

        val start = weekSchedules.minOfOrNull { it.start } ?: return null
        val end = weekSchedules.maxOfOrNull { it.end } ?: return null

        return Pair(start, end)
    }

    fun addMinorLessons(user: UserDto, schedules: List<Schedule>): List<Schedule> {
        val (start, end) = getSchedulesDateRange(schedules) ?: return schedules
        val dayOfWeek = ScheduleUtils.getMinorDayOfWeek(schedules) ?: return schedules

        if (user.email == null) return schedules
        val hseAppLessons = getHseAppMinorLessonsByUser(user.email, dayOfWeek, start, end)

        return schedules.map {
            if (it.scheduleType == ScheduleType.QUARTER_SCHEDULE) return@map it

            val range = it.start.rangeTo(it.end)

            val lessons = hseAppLessons.filter {
                val time = it.time as ScheduledTime
                range.contains(time.date)
            }

            it.copy(
                lessons = (lessons + it.lessons)
                    .filterNot { it.lessonType == LessonType.COMMON_MINOR }
                    .sorted()
            )
        }
    }

   fun getAvailableSchedules(): List<ScheduleInfo> {
        return excelScheduleService.getAvailableSchedules()
    }

    fun getUserScheduleByTelegramId(telegramId: Long, start: LocalDate, end: LocalDate): Schedule {
        val user = userService.getByTelegramId(telegramId)

        val schedule = excelScheduleService.getUserSchedule(user, start, end)

        val schedules = excelScheduleService.getUserSchedules(user)
        val dayOfWeek = ScheduleUtils.getMinorDayOfWeek(schedules) ?: return schedule

        if (user.email == null) return schedule
        if (schedule.scheduleType == ScheduleType.QUARTER_SCHEDULE) return schedule

        val hseAppLessons = getHseAppMinorLessonsByUser(user.email, dayOfWeek, start, end)

        return schedule.copy(
            lessons = (schedule.lessons + hseAppLessons)
                .filterNot { it.lessonType == LessonType.COMMON_MINOR }
                .sorted()
        )
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