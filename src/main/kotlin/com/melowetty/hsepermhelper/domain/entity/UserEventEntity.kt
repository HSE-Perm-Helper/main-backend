package com.melowetty.hsepermhelper.domain.entity

import com.melowetty.hsepermhelper.model.UserEventType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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
