package com.melowetty.hsepermhelper.model.lesson

data class AvailableLessonForHiding(
    val lesson: String,
    val lessonType: LessonType,
    val subGroup: Int?
)
