package com.melowetty.hsepermhelper.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
open class CreatableFromJob(
    @Column(name = "created_by", nullable = false)
    val runId: String,
)
