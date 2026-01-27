package com.melowetty.hsepermhelper.domain.model.lesson

data class AvailableLessonForHiding(
    val lesson: String,
    val lessonType: LessonType,
    val subGroup: Int?
)
