package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model

import java.time.LocalDate

data class ParsedLessonInfo(
    val isSessionWeek: Boolean,
    val subject: String,
    val lessonInfo: String? = null,
    val additionalInfo: List<String>? = null,
    val isUnderlined: Boolean,
    val isHaveBuildingInfo: Boolean,
    val schedulePeriod: ClosedRange<LocalDate>
)
