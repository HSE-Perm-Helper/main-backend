package com.melowetty.hsepermhelper.timetable.model

import java.time.LocalDate

data class InternalTimetableInfo(
    val id: String,
    val number: Int?,
    val start: LocalDate,
    val end: LocalDate,
    val type: InternalTimetableType,
)
