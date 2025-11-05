package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import java.time.DayOfWeek
import java.time.LocalDate

class ScheduleUtils {
    companion object {
        fun normalizeSchedules(schedules: List<InternalTimetable>): List<InternalTimetable> {
            val sessionSchedules = schedules.filter { it.type == InternalTimetableType.BACHELOR_SESSION_TIMETABLE }
            if (sessionSchedules.size < 2) return schedules
            val filteredSchedules = schedules.filter { it.type != InternalTimetableType.BACHELOR_SESSION_TIMETABLE }
            val mergedSchedule = mergeSessionSchedules(sessionSchedules)
            return filteredSchedules + mergedSchedule
        }

        fun mergeSessionSchedules(sessionSchedules: List<InternalTimetable>): InternalTimetable {
            val sortedSchedules = sessionSchedules.toList().sortedBy { it.start }
            val start = sortedSchedules.first().start
            val end = sortedSchedules.last().end
            val mergedSchedule = InternalTimetable(
                id = sessionSchedules.first().id,
                type = sessionSchedules.first().type,
                educationType = sessionSchedules.first().educationType,
                start = start,
                end = end,
                number = sortedSchedules.first().number,
                lessons = sortedSchedules.flatMap { it.lessons }.sorted(),
                isParent = true,
                source = sortedSchedules.first().source,
            )
            return mergedSchedule
        }

        fun List<Schedule>.filterWeekSchedules(): List<Schedule> {
            return filter { it.scheduleType == ScheduleType.WEEK_SCHEDULE || it.scheduleType == ScheduleType.SESSION_SCHEDULE }
        }

        fun List<InternalTimetable>.filterWeekExcelSchedules(): List<InternalTimetable> {
            return filter { it.type != InternalTimetableType.BACHELOR_QUARTER_TIMETABLE }
        }

        fun getWeekScheduleByDate(schedules: List<InternalTimetable>, date: LocalDate): InternalTimetable? {
            return schedules
                .filterWeekExcelSchedules()
                .firstOrNull { it.start <= date && it.end >= date }
        }

        fun getWeekScheduleByDate(schedules: List<Schedule>, date: LocalDate): Schedule? {
            return schedules
                .filterWeekSchedules()
                .firstOrNull { it.start <= date && it.end >= date }
        }

        fun getLessonsAtDateInWeekSchedule(schedule: Schedule, date: LocalDate): List<Lesson> {
            return schedule.lessons.filter {
                (it.time as ScheduledTime).date.isEqual(date)
            }
        }

        fun getDifferentDaysByLessons(before: InternalTimetable, after: InternalTimetable): List<DayOfWeek> {
            val changedDays = mutableSetOf<DayOfWeek>()
            val daysForChecking = before.lessons.map { it.time.dayOfWeek }.toHashSet()
            daysForChecking.addAll(after.lessons.map { it.time.dayOfWeek })

            val groupedLessonsBefore = before.lessons.groupBy { it.time.dayOfWeek }
            val groupedLessonsAfter = after.lessons.groupBy { it.time.dayOfWeek }

            daysForChecking.forEach {
                val beforeLessons = groupedLessonsBefore[it] ?: listOf()
                val afterLessons = groupedLessonsAfter[it] ?: listOf()

                if (beforeLessons.toHashSet() != afterLessons.toHashSet()) {
                    changedDays.add(it)
                }
            }


            return changedDays.sorted()
        }
    }
}