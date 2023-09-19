package com.melowetty.hsepermhelper.repository.impl

import com.melowetty.hsepermhelper.events.PublicEvent
import com.melowetty.hsepermhelper.repository.EventRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class EventRepositoryImpl: EventRepository {
    private val events = LinkedList<PublicEvent>()
    override fun getAllEvents(): List<PublicEvent> {
        return events.toList()
    }

    override fun addEvent(event: PublicEvent) {
        events.add(event)
    }

    override fun deleteFirstEvent(): Boolean {
        if (events.size == 0) return false
        return events.removeFirst() != null
    }

    override fun deleteLastEvent(): Boolean {
        if (events.size == 0) return false
        return events.removeLast() != null
    }

    override fun clearEvents() {
        events.clear()
    }

}