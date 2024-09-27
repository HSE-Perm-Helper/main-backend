package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.model.Lesson
import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleType
import com.melowetty.hsepermhelper.model.ScheduledTime
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
    }
}