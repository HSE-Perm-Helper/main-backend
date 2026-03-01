package com.melowetty.hsepermhelper.persistence.entity

import com.melowetty.hsepermhelper.domain.model.timetable.EducationType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class EducationGroupEntity(
    @Column(name = "user_group", nullable = false)
    val group: String,

    @Column(name = "education_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val educationType: EducationType,
)