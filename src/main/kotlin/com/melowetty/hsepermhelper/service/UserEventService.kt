package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.messaging.broker.MessageBrokerService
import com.melowetty.hsepermhelper.persistence.repository.UserRepository
import com.melowetty.hsepermhelper.persistence.storage.EventStorage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ExecutorService

@Service
class UserEventService(
    private val userRepository: UserRepository,
    private val eventStorage: EventStorage,
) {

    fun addUserEvent(telegramId: Long, eventType: UserEventType) {
        val userId = userRepository.getIdByTelegramId(telegramId)
            ?: run {
                logger.warn { "User with telegramId $telegramId not found" }
                return
            }

        eventStorage.saveEvent(userId, eventType)
    }

    fun addUserEvent(userId: UUID, eventType: UserEventType) {
        eventStorage.saveEvent(userId, eventType)
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}