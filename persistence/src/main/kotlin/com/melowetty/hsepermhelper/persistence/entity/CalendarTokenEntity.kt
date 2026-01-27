package com.melowetty.hsepermhelper.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "token")
class CalendarTokenEntity(
    @Id
    val userId: UUID,

    @Column(length = 64, nullable = false)
    var token: String,

    var lastFetch: LocalDateTime?
) {
    override fun toString(): String = "CalendarTokenEntity(userId=$userId, token='$token', lastFetch=$lastFetch)"

    override fun equals(other: Any?): Boolean {
        return other is CalendarTokenEntity && other.userId == userId
    }

    override fun hashCode(): Int = userId.hashCode()
}