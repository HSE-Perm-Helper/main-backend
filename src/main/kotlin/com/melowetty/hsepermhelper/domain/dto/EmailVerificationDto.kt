package com.melowetty.hsepermhelper.domain.dto

import java.time.LocalDateTime

data class EmailVerificationDto(
    val token: String,
    val nextAttempt: LocalDateTime?
)
