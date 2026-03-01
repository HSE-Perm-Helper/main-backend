package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model

import com.melowetty.hsepermhelper.domain.model.lesson.LessonTime

data class CellInfo(
    val value: String,
    val isUnderlined: Boolean,
    val group: String,
    val time: LessonTime
)
