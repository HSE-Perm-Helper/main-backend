package com.melowetty.hsepermhelper.persistence.projection

import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import java.util.UUID

data class HideLessonRecord(
    val userId: UUID,
    val lesson: String,
    val lessonType: LessonType,
    val subGroup: Int?
)
