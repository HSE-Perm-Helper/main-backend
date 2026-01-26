package com.melowetty.hsepermhelper.service.user

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.persistence.storage.EventStorage
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserEventService(
    private val eventStorage: EventStorage,
) {
    fun addUserEvent(userId: UUID, eventType: UserEventType) {
        eventStorage.saveEvent(userId, eventType)
    }
}