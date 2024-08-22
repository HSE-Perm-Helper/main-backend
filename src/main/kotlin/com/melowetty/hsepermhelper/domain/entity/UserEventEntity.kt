package com.melowetty.hsepermhelper.domain.entity

import com.melowetty.hsepermhelper.model.UserEventType
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@Entity
data class UserEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "date")
    val date: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    val targetUser: UserEntity,

    @Column(name = "user_event_type")
    val userEventType: UserEventType,
)
