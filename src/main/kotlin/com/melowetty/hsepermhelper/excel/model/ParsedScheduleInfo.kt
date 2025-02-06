package com.melowetty.hsepermhelper.excel.model

import com.melowetty.hsepermhelper.model.schedule.ScheduleType
import java.time.LocalDate

data class ParsedScheduleInfo(
    val number: Int?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val type: ScheduleType,
)
