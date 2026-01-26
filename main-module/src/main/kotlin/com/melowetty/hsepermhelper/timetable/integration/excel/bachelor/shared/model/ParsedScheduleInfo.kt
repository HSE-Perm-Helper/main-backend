package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model

import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType
import java.time.LocalDate

data class ParsedScheduleInfo(
    val number: Int?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val type: InternalTimetableType,
)
