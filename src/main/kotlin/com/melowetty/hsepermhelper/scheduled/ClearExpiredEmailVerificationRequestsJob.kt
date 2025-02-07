package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.repository.EmailVerificationRepository
import java.time.LocalDateTime
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ClearExpiredEmailVerificationRequestsJob(
    private val emailVerificationRepository: EmailVerificationRepository
) {
    companion object {
        private const val REQUEST_LIFE_TIME_IN_DAYS = 1
    }

    @Scheduled(cron = "0 0 * * * *")
    fun clearEmailVerifications() {
        val dateForClear = LocalDateTime.now().minusDays(REQUEST_LIFE_TIME_IN_DAYS.toLong())

        emailVerificationRepository.deleteByCreatedLessThanEqual(dateForClear)
    }
}