package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.messaging.broker.MessageBrokerService
import com.melowetty.hsepermhelper.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserEventService(
    private val userRepository: UserRepository,
    private val messageBrokerService: MessageBrokerService,
) {

    fun addUserEvent(telegramId: Long, eventType: UserEventType) {
        val userId = userRepository.getIdByTelegramId(telegramId)
            ?: run {
                logger.warn { "User with telegramId $telegramId not found" }
                return
            }

        messageBrokerService.sendUserEvent(userId, eventType)
    }

    fun addUserEvent(userId: UUID, eventType: UserEventType) {
        messageBrokerService.sendUserEvent(userId, eventType)
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}