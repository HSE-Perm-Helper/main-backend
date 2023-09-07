package com.melowetty.hsepermhelper.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "telegram_id")
    val telegramId: Long = 0L,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    val settings: SettingsEntity? = null,
)