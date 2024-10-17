package com.melowetty.hsepermhelper.model

data class AvailableLessonForHiding(
    val lesson: String,
    val lessonType: LessonType,
    val subGroup: Int?
)
