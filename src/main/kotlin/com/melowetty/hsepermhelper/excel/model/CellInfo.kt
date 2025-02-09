package com.melowetty.hsepermhelper.excel.model

import com.melowetty.hsepermhelper.domain.model.lesson.LessonTime

data class CellInfo(
    val value: String,
    val isUnderlined: Boolean,
    val course: Int,
    val program: String,
    val group: String,
    val time: LessonTime
)
