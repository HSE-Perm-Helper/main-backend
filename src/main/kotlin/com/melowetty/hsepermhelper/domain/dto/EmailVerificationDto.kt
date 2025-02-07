package com.melowetty.hsepermhelper.domain.dto

import java.time.LocalDateTime

data class EmailVerificationDto(
    val token: String,
    val created: LocalDateTime,
    val nextAttemptIn: Int
)
