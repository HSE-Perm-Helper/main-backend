package com.melowetty.hsepermhelper.remote.service

import com.melowetty.hsepermhelper.persistence.storage.CalendarTokenStorage
import com.melowetty.hsepermhelper.remote.exception.CalendarTokenNotFoundException
import java.util.UUID
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service

@Service
class RemoteTimetableManagementService(
    private val calendarTokenStorage: CalendarTokenStorage
) {
    fun getUserIdByToken(token: String): UUID? {
        return calendarTokenStorage.getUserIdByToken(token)
    }

    fun createOrUpdateToken(userId: UUID): String {
        val generatedToken = generateToken()

        if (calendarTokenStorage.existsTokenByUserId(userId)) {
            calendarTokenStorage.updateUserToken(userId, generatedToken)
            return generatedToken
        }

        calendarTokenStorage.saveUserToken(userId, generatedToken)
        return generatedToken
    }

    fun getToken(userId: UUID): String {
        val token = calendarTokenStorage.getUserToken(userId)
            ?: throw CalendarTokenNotFoundException()

        return token
    }

    fun generateToken(): String {
        return RandomStringUtils.secure().nextAlphanumeric(32)
    }

    fun markTokenAsUsed(token: String) {
        calendarTokenStorage.markTokenAsUsed(token)
    }
}