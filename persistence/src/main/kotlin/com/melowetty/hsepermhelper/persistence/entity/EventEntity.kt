package com.melowetty.hsepermhelper.persistence.entity

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "event")
class EventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long?,

    @Column(name = "source", nullable = true)
    val userId: UUID?,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: UserEventType,

    @Column(name = "timestamp", nullable = false)
    val created: LocalDateTime,
) {
    fun id() = id ?: throw IllegalStateException("EventEntity is not persisted")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "EventEntity(id=$id, userId=$userId, type=$type, created=$created)"
    }

    companion object {
        fun of(userId: UUID?, type: UserEventType) = EventEntity(null, userId, type, LocalDateTime.now())
    }
}