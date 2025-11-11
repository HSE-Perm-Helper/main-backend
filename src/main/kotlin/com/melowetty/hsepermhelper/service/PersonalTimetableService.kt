package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.timetable.TimetableComposer
import com.melowetty.hsepermhelper.util.DateUtils
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

@Service
class PersonalTimetableService(
    private val userService: UserService,
    private val timetableComposer: TimetableComposer,
) {
    fun getTimetables(userId: UUID): List<ScheduleInfo> {
        val user = userService.getUserRecordById(userId)
        return timetableComposer.getAvailableTimetables(user)
    }

    fun getTodayLessons(userId: UUID): List<Lesson> {
        val user = userService.getUserRecordById(userId)
        val timetables = timetableComposer.getAvailableTimetables(user).filterNonWeekTimetables()

        val todayDate = LocalDate.now(DateUtils.PERM_TIME_ZONE.toZoneId())

        val timetable = getTimetableForDate(user, timetables, todayDate)
        return getLessonsAtDate(timetable, todayDate)
    }

    fun getTomorrowLessons(userId: UUID): List<Lesson> {
        val user = userService.getUserRecordById(userId)
        val timetables = timetableComposer.getAvailableTimetables(user).filterNonWeekTimetables()

        var tomorrowDate = LocalDate.now(DateUtils.PERM_TIME_ZONE.toZoneId()).plusDays(1)
        if (tomorrowDate.dayOfWeek == DayOfWeek.SUNDAY) tomorrowDate = tomorrowDate.plusDays(1)

        val timetable = getTimetableForDate(user, timetables, tomorrowDate)

        return getLessonsAtDate(timetable, tomorrowDate)
    }

    fun getTimetable(userId: UUID, timetableId: String): Schedule {
        val user = userService.getUserRecordById(userId)
        return timetableComposer.getTimetable(timetableId, user)
    }

    fun getLessonsForHiding(userId: UUID): List<AvailableLessonForHiding> {
        val user = userService.getUserRecordById(userId)
        val bannedLessonTypes = setOf(LessonType.EVENT)

        return timetableComposer.getAllLessons(user).map {
            AvailableLessonForHiding(lesson = it.subject, lessonType = it.lessonType, subGroup = it.subGroup)
        }
            .filterNot { it.lessonType in bannedLessonTypes }
            .distinct()
            .sortedWith(compareBy({ it.lesson }, { it.lessonType }, { it.subGroup ?: 0 }))
    }

    private fun List<ScheduleInfo>.filterNonWeekTimetables(): List<ScheduleInfo> {
        return this.filter { it.scheduleType != ScheduleType.QUARTER_SCHEDULE }
    }

    private fun getTimetableForDate(user: UserRecord, timetables: List<ScheduleInfo>, date: LocalDate): Schedule {
        val targetTimetable = timetables
            .firstOrNull { date >= it.start && date <= it.end }
            ?: throw ScheduleNotFoundException("No timetable for date $date")

        return timetableComposer.getTimetable(targetTimetable.id, user)
    }

    private fun getLessonsAtDate(timetable: Schedule, date: LocalDate): List<Lesson> {
        return timetable.lessons.filter { it.time is ScheduledTime }.filter {
            val time = it.time as ScheduledTime
            time.date == date
        }
    }
}