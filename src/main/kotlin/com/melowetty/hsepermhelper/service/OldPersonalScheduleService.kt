package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.extension.LessonExtensions.Companion.toLesson
import com.melowetty.hsepermhelper.util.DateUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils
import com.melowetty.hsepermhelper.util.ScheduleUtils.Companion.filterWeekSchedules
import java.time.DayOfWeek
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
@Deprecated("Refactor when new schedule flow is implemented")
class OldPersonalScheduleService(
    private val excelScheduleService: ExcelScheduleService,
    private val hseAppApiService: HseAppApiService,
    private val oldUserService: OldUserService,
) {
    fun getScheduleByGroup(group: String) = excelScheduleService.getScheduleByGroup(group)

    private fun getHseAppMinorLessonsByUser(studentEmail: String, from: LocalDate, to: LocalDate): List<Lesson> {
        return try {
            hseAppApiService.getLessons(studentEmail, from, to)
                .filter { it.isMinor }
                .map { it.toLesson() }
        } catch (e: RuntimeException) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getUserSchedulesByTelegramId(telegramId: Long): List<Schedule> {
        val user = oldUserService.getByTelegramId(telegramId)

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

        if (user.email == null) return schedules
        val hseAppLessons = getHseAppMinorLessonsByUser(user.email, start, end)

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
}