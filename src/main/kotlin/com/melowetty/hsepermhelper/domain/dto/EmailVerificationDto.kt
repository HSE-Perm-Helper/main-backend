package com.melowetty.hsepermhelper.domain.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.util.DateUtils
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class EmailVerificationDto(
    val token: String,
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    val nextAttempt: LocalDateTime?,
    val nextAttemptIn: Int? =
        nextAttempt?.let { 0.coerceAtLeast(ChronoUnit.SECONDS.between(nextAttempt, LocalDateTime.now()).toInt()) }
)
