package com.melowetty.hsepermhelper.domain.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType

data class HideLessonDto(
    @JsonIgnore // TODO убрать это зачем это вообще надо и проверить entity
    val id: Long,

    val lesson: String,

    val lessonType: LessonType,

    val subGroup: Int?
)
