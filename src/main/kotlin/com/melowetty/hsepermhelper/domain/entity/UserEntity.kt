package com.melowetty.hsepermhelper.domain.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.UUID
import org.springframework.data.annotation.CreatedDate

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "telegram_id", unique = true)
    val telegramId: Long = 0L,

    @Column
    val email: String? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "settings_id")
    val settings: SettingsEntity,

    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now()
)