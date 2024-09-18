package com.melowetty.hsepermhelper.excel.model

import java.time.LocalDate

data class ParsedExcelInfo(
    val number: Int?,
    val start: LocalDate,
    val end: LocalDate
)
