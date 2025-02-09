package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.excel.model.ExcelSchedule
import com.melowetty.hsepermhelper.extension.LessonExtensions.Companion.toLesson

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