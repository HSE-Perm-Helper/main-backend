package com.melowetty.hsepermhelper.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "email_verification")
class EmailVerificationEntity(
    @Id
    @Column(length = 10, unique = true)
    val token: String,

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    val email: String,
    val created: LocalDateTime,
    var attempts: Int,
    var nextAttempt: LocalDateTime?,

    @Column(length = 24, unique = true)
    val secret: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmailVerificationEntity

        return token == other.token
    }

    override fun hashCode(): Int {
        return token.hashCode()
    }
}