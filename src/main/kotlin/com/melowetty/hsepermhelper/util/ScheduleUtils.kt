package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.model.excel.ExcelLesson
import com.melowetty.hsepermhelper.model.excel.ExcelSchedule
import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.model.lesson.LessonType
import com.melowetty.hsepermhelper.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.schedule.ScheduleType
import java.time.DayOfWeek
import java.time.LocalDate

class ScheduleUtils {
    companion object {
        fun normalizeSchedules(schedules: List<ExcelSchedule>): List<ExcelSchedule> {
            val sessionSchedules = schedules.filter { it.scheduleType == ScheduleType.SESSION_SCHEDULE }
            if (sessionSchedules.size < 2) return schedules
            val filteredSchedules = schedules.filter { it.scheduleType != ScheduleType.SESSION_SCHEDULE }
            val mergedSchedule = mergeSessionSchedules(sessionSchedules)
            return filteredSchedules + mergedSchedule
        }

        fun mergeSessionSchedules(sessionSchedules: List<ExcelSchedule>): ExcelSchedule {
            val sortedSchedules = sessionSchedules.toList().sortedBy { it.start }
            val start = sortedSchedules.first().start
            val end = sortedSchedules.last().end
            val mergedSchedule = ExcelSchedule(
                scheduleType = ScheduleType.SESSION_SCHEDULE,
                start = start,
                end = end,
                number = sortedSchedules.first().number,
                lessons = sortedSchedules.flatMap { it.lessons }.sorted()
            )
            return mergedSchedule
        }

        fun List<Schedule>.filterWeekSchedules(): List<Schedule> {
            return filter { it.scheduleType == ScheduleType.WEEK_SCHEDULE || it.scheduleType == ScheduleType.SESSION_SCHEDULE }
        }

        fun List<ExcelSchedule>.filterWeekExcelSchedules(): List<ExcelSchedule> {
            return filter { it.scheduleType == ScheduleType.WEEK_SCHEDULE || it.scheduleType == ScheduleType.SESSION_SCHEDULE }
        }

        fun getWeekScheduleByDate(schedules: List<ExcelSchedule>, date: LocalDate): ExcelSchedule? {
            return schedules
                .filterWeekExcelSchedules()
                .firstOrNull { it.start <= date && it.end >= date }
        }

        fun getWeekScheduleByDate(schedules: List<Schedule>, date: LocalDate): Schedule? {
            return schedules
                .filterWeekSchedules()
                .firstOrNull { it.start <= date && it.end >= date }
        }

        fun getLessonsAtDateInWeekSchedule(schedule: ExcelSchedule, date: LocalDate): List<ExcelLesson> {
            return schedule.lessons.filter {
                (it.time as ScheduledTime).date.isEqual(date)
            }
        }

        fun getLessonsAtDateInWeekSchedule(schedule: Schedule, date: LocalDate): List<com.melowetty.hsepermhelper.domain.model.lesson.Lesson> {
            return schedule.lessons.filter {
                (it.time as ScheduledTime).date.isEqual(date)
            }
        }

        fun getCourseFromGroup(group: String): Int {
            val dividedGroup = group.split("-")
            val year = dividedGroup[1].toInt()
            return 25 - year
        }

        fun getShortGroupFromGroup(group: String): String {
            return group.split("-")[0]
        }

        fun getDifferentDaysByLessons(before: ExcelSchedule, after: ExcelSchedule): List<DayOfWeek> {
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

        fun getMinorDayOfWeek(schedules: List<Schedule>): DayOfWeek? {
            return schedules.map { it.lessons }
                .flatten()
                .firstOrNull { it.lessonType == LessonType.COMMON_MINOR }?.time?.dayOfWeek
        }
    }
}