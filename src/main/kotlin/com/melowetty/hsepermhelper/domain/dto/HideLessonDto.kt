package com.melowetty.hsepermhelper.domain.dto

import com.melowetty.hsepermhelper.model.LessonType

data class HideLessonDto(
    val id: Long,

    val lesson: String,

    val lessonType: LessonType,

    val subGroup: Int?
)
