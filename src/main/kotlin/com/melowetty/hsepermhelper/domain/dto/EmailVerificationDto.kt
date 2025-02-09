package com.melowetty.hsepermhelper.domain.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.util.DateUtils
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class EmailVerificationDto(
    val token: String,
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    val nextAttempt: LocalDateTime?,
    val nextAttemptIn: Int? = getNextAttemptIn(nextAttempt)
) {
    companion object {
        fun getNextAttemptIn(nextAttempt: LocalDateTime?): Int? {
            nextAttempt ?: return null
            val currentDate = LocalDateTime.now()

            val seconds = ChronoUnit.SECONDS.between(nextAttempt, currentDate).toInt()

            if (currentDate >= nextAttempt) {
                return 0
            }

            return seconds
        }
    }
}
