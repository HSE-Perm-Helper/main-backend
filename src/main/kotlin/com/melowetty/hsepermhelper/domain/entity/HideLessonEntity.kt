package com.melowetty.hsepermhelper.domain.entity

import com.melowetty.hsepermhelper.model.LessonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "hide_lesson")
data class HideLessonEntity(
    @Id
    val id: Long,

    @Column(nullable = false)
    val lesson: String,

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    val lessonType: LessonType,

    val subGroup: Int?

)
