package com.melowetty.hsepermhelper.utils

import com.melowetty.hsepermhelper.models.Schedule
import com.melowetty.hsepermhelper.models.ScheduleType

class ScheduleUtils {
    companion object {
        fun normalizeSchedules(schedules: List<Schedule>): List<Schedule> {
            val sessionSchedules = schedules.filter { it.scheduleType == ScheduleType.SESSION_SCHEDULE }
            if(sessionSchedules.size < 2) return schedules
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
    }
}