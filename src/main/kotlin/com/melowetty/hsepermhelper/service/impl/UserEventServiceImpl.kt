package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.model.UserEventType
import com.melowetty.hsepermhelper.service.UserEventService
import com.melowetty.hsepermhelper.service.UserService
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class UserEventServiceImpl(
    private val userService: UserService,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) : UserEventService {
    override fun addUserEvent(telegramId: Long, eventType: UserEventType) {
        val user = userService.getByTelegramId(telegramId)
        kafkaTemplate.send("user-events", eventType.toString(), mapOf("source" to user.id))
    }
}