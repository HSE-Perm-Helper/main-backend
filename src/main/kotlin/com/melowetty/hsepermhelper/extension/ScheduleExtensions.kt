package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.extension.LessonExtensions.Companion.toLesson
import com.melowetty.hsepermhelper.model.excel.ExcelSchedule
import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.schedule.ScheduleInfo

class ScheduleExtensions {
    companion object {
        fun Schedule.toScheduleInfo(): ScheduleInfo {
            return ScheduleInfo(
                number = number,
                start = start,
                end = end,
                scheduleType = scheduleType
            )
        }

        fun ExcelSchedule.toScheduleInfo(): ScheduleInfo {
            return ScheduleInfo(
                number = number,
                start = start,
                end = end,
                scheduleType = scheduleType
            )
        }

        fun ExcelSchedule.toSchedule(): Schedule {
            return Schedule(
                number = number,
                start = start,
                end = end,
                scheduleType = scheduleType,
                lessons = lessons.map { it.toLesson() }
            )
        }
    }
}