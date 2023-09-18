package com.melowetty.hsepermhelper.entity

import com.melowetty.hsepermhelper.models.UserEventType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class UserEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "date")
    val date: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "user_id")
    val targetUser: UserEntity,

    @Column(name = "user_event_type")
    val userEventType: UserEventType,
)
