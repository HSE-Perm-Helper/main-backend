package com.melowetty.hsepermhelper.model.lesson

import java.time.LocalDateTime

data class HseAppLesson(
    val type: LessonType,
    val subject: String,
    val subjectLink: String,
    val dateStart: LocalDateTime,
    val dateEnd: LocalDateTime,
    val streamLinks: List<String>?,
    val lecturers: List<String>,
    val note: String?
)
