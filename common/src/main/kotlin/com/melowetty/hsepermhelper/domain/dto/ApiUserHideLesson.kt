package com.melowetty.hsepermhelper.domain.dto

import com.melowetty.hsepermhelper.domain.model.lesson.LessonType

data class ApiUserHideLesson(
    val lesson: String,
    val lessonType: LessonType,
    val subGroup: Int?
)
