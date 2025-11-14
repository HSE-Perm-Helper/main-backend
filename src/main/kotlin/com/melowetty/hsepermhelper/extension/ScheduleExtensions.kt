package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.extension.LessonExtensions.Companion.toLesson
import com.melowetty.hsepermhelper.timetable.model.InternalLesson
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType

// TODO: make as object
class ScheduleExtensions {
    companion object {
        fun List<InternalLesson>.computeHash(): Int {
            return this.sumOf {
                it.hashCode()
            }
        }

        fun InternalTimetable.toInfo(): InternalTimetableInfo {
            return InternalTimetableInfo(
                id = id ?: "",
                number = number,
                start = start,
                end = end,
                type = type,
                educationType = educationType,
                isParent = isParent,
                lessonsHash = lessonsHash,
                source = source,
            )
        }

        fun InternalTimetable.toScheduleInfo(): ScheduleInfo {
            return ScheduleInfo(
                id = id ?: "",
                number = number,
                start = start,
                end = end,
                scheduleType = type.toScheduleType()
            )
        }

        fun InternalTimetable.toSchedule(): Schedule {
            return Schedule(
                id = id ?: "",
                number = number,
                start = start,
                end = end,
                scheduleType = type.toScheduleType(),
                lessons = lessons.map { it.toLesson() }
            )
        }

        fun InternalTimetableType.toScheduleType(): ScheduleType {
            return when (this) {
                InternalTimetableType.BACHELOR_WEEK_TIMETABLE -> ScheduleType.WEEK_SCHEDULE
                InternalTimetableType.BACHELOR_SESSION_TIMETABLE -> ScheduleType.SESSION_SCHEDULE
                InternalTimetableType.BACHELOR_QUARTER_TIMETABLE -> ScheduleType.QUARTER_SCHEDULE
                InternalTimetableType.BACHELOR_ENGLISH_TIMETABLE -> throw IllegalArgumentException("Timetable type is not able to display")
            }
        }
    }
}