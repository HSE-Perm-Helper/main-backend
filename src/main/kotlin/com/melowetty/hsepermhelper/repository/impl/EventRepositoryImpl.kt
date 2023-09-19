package com.melowetty.hsepermhelper.repository.impl

import com.melowetty.hsepermhelper.events.PublicEvent
import com.melowetty.hsepermhelper.repository.EventRepository
import org.springframework.stereotype.Repository

@Repository
class EventRepositoryImpl: EventRepository {
    private val events = mutableMapOf <Long, PublicEvent>()
    override fun getAllEvents(): List<PublicEvent> {
        return events.values.toList()
    }

    override fun deleteEvent(id: Long): Boolean {
        return events.remove(id) != null
    }

    override fun deleteEvents(ids: List<Long>): Boolean {
        return ids.map { deleteEvent(it) }.all { it }
    }

    override fun clearEvents() {
        events.clear()
    }

}