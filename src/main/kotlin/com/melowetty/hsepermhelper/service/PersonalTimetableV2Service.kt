package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.timetable.TimetableComposer
import com.melowetty.hsepermhelper.util.DateUtils
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

@Service
class PersonalTimetableV2Service(
    private val userService: UserService,
    private val timetableComposer: TimetableComposer,
) {
    fun getTimetables(userId: UUID): List<ScheduleInfo> {
        val user = userService.getById(userId)
        return timetableComposer.getAvailableTimetables(user)
    }

    fun getTodayLessons(userId: UUID): List<Lesson> {
        val user = userService.getById(userId)
        val timetables = timetableComposer.getAvailableTimetables(user).filterNonWeekTimetables()

        val todayDate = LocalDate.now(DateUtils.PERM_TIME_ZONE.toZoneId())

        val timetable = getTimetableForDate(user, timetables, todayDate)
        return  getLessonsAtDate(timetable, todayDate)
    }

    fun getTomorrowLessons(userId: UUID): List<Lesson> {
        val user = userService.getById(userId)
        val timetables = timetableComposer.getAvailableTimetables(user).filterNonWeekTimetables()

        var tomorrowDate = LocalDate.now(DateUtils.PERM_TIME_ZONE.toZoneId()).plusDays(1)
        if (tomorrowDate.dayOfWeek == DayOfWeek.SUNDAY) tomorrowDate = tomorrowDate.plusDays(1)

        val timetable = getTimetableForDate(user, timetables, tomorrowDate)

        return  getLessonsAtDate(timetable, tomorrowDate)
    }

    fun getTimetable(userId: UUID, timetableId: String): Schedule {
        val user = userService.getById(userId)
        return timetableComposer.getTimetable(timetableId, user)
    }

    fun getLessonsForHiding(userId: UUID): List<AvailableLessonForHiding> {
        val user = userService.getById(userId)
        return timetableComposer.getAllLessons(user).map {
            AvailableLessonForHiding(lesson = it.subject, lessonType = it.lessonType, subGroup = it.subGroup)
        }
            .distinct()
            .sortedBy { it.lessonType }
            .sortedBy { it.lesson }
    }

    private fun List<ScheduleInfo>.filterNonWeekTimetables(): List<ScheduleInfo> {
        return this.filter { it.scheduleType != ScheduleType.QUARTER_SCHEDULE }
    }

    private fun getTimetableForDate(user: UserDto, timetables: List<ScheduleInfo>, date: LocalDate): Schedule {
        val targetTimetable = timetables.firstOrNull { it.start >= date }
            ?: throw ScheduleNotFoundException("No timetable for tomorrow")

        return timetableComposer.getTimetable(targetTimetable.id, user)
    }

    private fun getLessonsAtDate(timetable: Schedule, date: LocalDate): List<Lesson> {
        return timetable.lessons.filter { it.time is ScheduledTime }.filter {
            val time = it.time as ScheduledTime
            time.date == date
        }
    }
}