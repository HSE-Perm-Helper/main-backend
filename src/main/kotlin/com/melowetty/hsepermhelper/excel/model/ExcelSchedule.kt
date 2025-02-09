package com.melowetty.hsepermhelper.excel.model

import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import java.time.LocalDate

data class ExcelSchedule(
    val number: Int?,
    val lessons: List<ExcelLesson>,
    val start: LocalDate,
    val end: LocalDate,
    val scheduleType: ScheduleType,
)
