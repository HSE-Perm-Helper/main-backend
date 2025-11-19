package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model

import java.time.LocalDate

data class ParsedExcelInfo(
    val number: Int?,
    val start: LocalDate,
    val end: LocalDate
)
