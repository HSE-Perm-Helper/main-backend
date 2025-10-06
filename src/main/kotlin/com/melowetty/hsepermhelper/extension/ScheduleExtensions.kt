package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.extension.LessonExtensions.Companion.toLesson
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType

class ScheduleExtensions {
    companion object {
        fun InternalTimetable.toInfo(): InternalTimetableInfo {
            return InternalTimetableInfo(
                id = id(),
                number = number,
                start = start,
                end = end,
                type = type
            )
        }

        fun InternalTimetable.toScheduleInfo(): ScheduleInfo {
            return ScheduleInfo(
                number = number,
                start = start,
                end = end,
                scheduleType = type.toScheduleType()
            )
        }

        fun InternalTimetable.toSchedule(): Schedule {
            return Schedule(
                number = number,
                start = start,
                end = end,
                scheduleType = type.toScheduleType(),
                lessons = lessons.map { it.toLesson() }
            )
        }

        fun InternalTimetableType.toScheduleType(): ScheduleType {
            return when (this) {
                InternalTimetableType.BACHELOR_WEEK_SCHEDULE -> ScheduleType.WEEK_SCHEDULE
                InternalTimetableType.BACHELOR_SESSION_SCHEDULE -> ScheduleType.SESSION_SCHEDULE
                InternalTimetableType.BACHELOR_QUARTER_SCHEDULE -> ScheduleType.QUARTER_SCHEDULE
            }
        }
    }
}