package com.melowetty.hsepermhelper.domain.entity

import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "hide_lesson")
data class HideLessonEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(nullable = false)
    val lesson: String = "",

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    val lessonType: LessonType = LessonType.TEST,

    val subGroup: Int? = null,

    ) {
    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  lesson = $lesson   ,   lessonType = $lessonType   ,   subGroup = $subGroup )"
    }
}
