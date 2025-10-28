package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class UserEventService(
    private val messageBrokerService: MessageBrokerService,
) {
    @Deprecated("Remove lazy init")
    @Autowired
    @Lazy
    private lateinit var userService: UserService

    fun addUserEvent(telegramId: Long, eventType: UserEventType) {
        val user = userService.getByTelegramId(telegramId)
        messageBrokerService.sendUserEvent(user.id, eventType)
    }
}