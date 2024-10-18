package com.melowetty.hsepermhelper.domain.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.melowetty.hsepermhelper.model.LessonType

data class HideLessonDto(
    @JsonIgnore
    val id: Long,

    val lesson: String,

    val lessonType: LessonType,

    val subGroup: Int?
)
