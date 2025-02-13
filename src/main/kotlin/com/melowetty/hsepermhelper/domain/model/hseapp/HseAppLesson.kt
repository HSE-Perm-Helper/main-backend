package com.melowetty.hsepermhelper.domain.model.hseapp

import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import java.time.LocalDateTime

data class HseAppLesson(
    val type: LessonType,
    val subject: String,
    val subjectLink: String,
    val dateStart: LocalDateTime,
    val dateEnd: LocalDateTime,
    val streamLinks: List<String>?,
    val lecturers: List<String>,
    val note: String?,
    val auditorium: String?,
    val isMinor: Boolean
)
