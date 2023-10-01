package com.melowetty.hsepermhelper.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class DataEntity(
    @Id
    val key: String,
    val value: String,
)