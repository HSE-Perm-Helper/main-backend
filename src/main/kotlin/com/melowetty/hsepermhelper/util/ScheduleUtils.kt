package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.model.lesson.Lesson
import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.model.lesson.ScheduledTime
import java.time.DayOfWeek
import java.time.LocalDate

class ScheduleUtils {
    companion object {
        fun normalizeSchedules(schedules: List<Schedule>): List<Schedule> {
            val sessionSchedules = schedules.filter { it.scheduleType == ScheduleType.SESSION_SCHEDULE }
            if (sessionSchedules.size < 2) return schedules
            val filteredSchedules = schedules.filter { it.scheduleType != ScheduleType.SESSION_SCHEDULE }
            val mergedSchedule = mergeSessionSchedules(sessionSchedules)
            return filteredSchedules + mergedSchedule
        }

        fun mergeSessionSchedules(sessionSchedules: List<Schedule>): Schedule {
            val sortedSchedules = sessionSchedules.toList().sortedBy { it.start }
            val start = sortedSchedules.first().start
            val end = sortedSchedules.last().end
            val mergedSchedule = Schedule(
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

        fun getCourseFromGroup(group: String): Int {
            val dividedGroup = group.split("-")
            val year = dividedGroup[1].toInt()
            return 25 - year
        }

        fun getShortGroupFromGroup(group: String): String {
            return group.split("-")[0]
        }

        fun getDifferentDaysByLessons(before: Schedule, after: Schedule): List<DayOfWeek> {
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