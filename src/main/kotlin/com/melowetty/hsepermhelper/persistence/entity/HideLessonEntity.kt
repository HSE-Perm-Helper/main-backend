package com.melowetty.hsepermhelper.persistence.entity

import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "hide_lesson")
class HideLessonEntity(
    @EmbeddedId
    val id: HideLessonId,
) {
    override fun equals(other: Any?): Boolean {
        return id == (other as? HideLessonEntity)?.id
    }

    override fun hashCode(): Int = id.hashCode()
}

@Embeddable
data class HideLessonId(
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "lesson", nullable = false)
    val lesson: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", nullable = false)
    val lessonType: LessonType,

    @Column(name = "sub_group", nullable = false)
    val subGroup: Int,
)
