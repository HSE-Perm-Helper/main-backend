package com.melowetty.hsepermhelper.persistence.storage

import com.melowetty.hsepermhelper.persistence.entity.CalendarTokenEntity
import com.melowetty.hsepermhelper.persistence.repository.CalendarTokenRepository
import java.time.LocalDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrNull
import org.springframework.stereotype.Component

@Component
class CalendarTokenStorage(
    private val calendarTokenRepository: CalendarTokenRepository
) {

    fun getUserIdByToken(token: String): UUID? {
        return calendarTokenRepository.findByToken(token)?.userId
    }

    fun existsToken(token: String): Boolean {
        return calendarTokenRepository.findByToken(token) != null
    }

    fun existsTokenByUserId(userId: UUID): Boolean {
        return calendarTokenRepository.existsById(userId)
    }

    fun markTokenAsUsed(token: String) {
        val tokenEntity = calendarTokenRepository.findByToken(token)
            ?: return

        tokenEntity.lastFetch = LocalDateTime.now()

        calendarTokenRepository.save(tokenEntity)
    }

    fun getUserToken(userId: UUID): String? {
        val tokenEntity = calendarTokenRepository.findById(userId).getOrNull()

        return tokenEntity?.token
    }

    fun saveUserToken(userId: UUID, token: String) {
        val tokenEntity = CalendarTokenEntity(userId, token, null)

        calendarTokenRepository.save(tokenEntity)
    }

    fun updateUserToken(userId: UUID, token: String) {
        val entity = calendarTokenRepository.findById(userId).getOrNull()
            ?: return

        entity.token = token

        calendarTokenRepository.save(entity)
    }
}