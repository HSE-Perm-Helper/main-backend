package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.service.UserEventService
import com.melowetty.hsepermhelper.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class UserEventServiceImpl(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) : UserEventService {
    @Deprecated("Remove lazy init")
    @Autowired
    @Lazy
    private lateinit var userService: UserService

    override fun addUserEvent(telegramId: Long, eventType: UserEventType) {
        val user = userService.getByTelegramId(telegramId)
        kafkaTemplate.send("user-events", eventType.toString(), mapOf("source" to user.id))
    }
}