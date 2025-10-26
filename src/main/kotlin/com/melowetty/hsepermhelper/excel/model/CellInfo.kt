package com.melowetty.hsepermhelper.excel.model

import com.melowetty.hsepermhelper.domain.model.lesson.LessonTime

data class CellInfo(
    val value: String,
    val isUnderlined: Boolean,
    val group: String,
    val time: LessonTime
)
