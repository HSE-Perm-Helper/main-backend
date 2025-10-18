package com.melowetty.hsepermhelper.service.impl.event

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.service.UserEventService
import io.github.oshai.kotlinlogging.KotlinLogging

class StubUserEventService: UserEventService {
    override fun addUserEvent(telegramId: Long, eventType: UserEventType) {
        logger.info { "Stub user event added: $telegramId, $eventType" }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}