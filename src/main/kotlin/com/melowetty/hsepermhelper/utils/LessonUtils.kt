package com.melowetty.hsepermhelper.utils

import Schedule
import Schedule.Companion.toScheduleInfo
import com.melowetty.hsepermhelper.models.Lesson
import com.melowetty.hsepermhelper.models.ScheduleType

class LessonUtils {
    companion object {
        /**
         * Merge different schedules with deleting lessons intersection by schedule priority
         *
         * @param schedules list of schedules
         * @return list of lessons from schedules
         */
        fun mergeSchedules(schedules: List<Schedule>): List<Lesson> {
            val lessons = mutableListOf<Lesson>()
            schedules.asSequence().map {
                if(it.scheduleType == ScheduleType.QUARTER_SCHEDULE) unpackQuarterSchedule(it)
                else listOf(it)
            }.flatten().filter { it.lessons.isNotEmpty() }.groupBy { it.weekStart }.forEach { _, groupedSchedules ->
                val foundSchedule = groupedSchedules.maxByOrNull { it.scheduleType.priority }
                if(foundSchedule != null) {
                    lessons.addAll(foundSchedule.lessons.values.flatten())
                }
            }
            return lessons
        }

        private fun unpackQuarterSchedule(schedule: Schedule): List<Schedule> {
            val schedules = mutableListOf<Schedule>()
            val lessons = schedule.lessons.flatMap { it.value }
            var weekStart = schedule.weekStart.minusDays(schedule.weekStart.dayOfWeek.ordinal.toLong())
            var weekEnd = weekStart.plusDays(6)
            while (weekStart.isBefore(schedule.weekEnd)) {
                val newSchedule = schedule.copy(
                    weekStart = weekStart,
                    weekEnd = weekEnd,
                    lessons = lessons.filter { lesson ->
                        lesson.date.isBefore(weekEnd) && lesson.date.isAfter(weekStart)
                                || lesson.date == weekStart || lesson.date == weekEnd
                    }.groupBy { it.date }
                )
                schedules.add(newSchedule)
                weekStart = weekStart.plusDays(7)
                weekEnd = weekEnd.plusDays(7)
            }
            return schedules
        }
    }
}