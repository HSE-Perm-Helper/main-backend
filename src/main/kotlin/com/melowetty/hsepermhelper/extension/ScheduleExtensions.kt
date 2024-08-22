package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleInfo

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
    }
}