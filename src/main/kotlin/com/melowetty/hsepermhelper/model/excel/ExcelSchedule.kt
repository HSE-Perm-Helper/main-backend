package com.melowetty.hsepermhelper.model.excel

import com.melowetty.hsepermhelper.model.schedule.ScheduleType
import java.time.LocalDate

data class ExcelSchedule(
    val number: Int?,
    val lessons: List<ExcelLesson>,
    val start: LocalDate,
    val end: LocalDate,
    val scheduleType: ScheduleType,
)
