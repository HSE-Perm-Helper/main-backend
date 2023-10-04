package com.melowetty.hsepermhelper.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "service_data")
data class DataEntity(
    @Id
    val key: String,
    val value: String,
)