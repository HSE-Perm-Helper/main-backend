package com.melowetty.hsepermhelper.persistence.storage

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.persistence.entity.EventEntity
import com.melowetty.hsepermhelper.persistence.repository.EventRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EventStorage(
    private val eventRepository: EventRepository,
    private val userStorage: UserStorage,
) {
    fun saveEvent(userId: UUID, type: UserEventType) {
        if (!userStorage.existsUserById(userId)) {
            throw UserByIdNotFoundException(userId)
        }

        val entity = EventEntity.of(userId, type)
        eventRepository.save(entity)
        logger.info { "Saved event $type for user $userId" }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}