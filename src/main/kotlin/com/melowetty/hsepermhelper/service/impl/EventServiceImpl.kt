package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.events.common.PublicEvent
import com.melowetty.hsepermhelper.repository.EventRepository
import com.melowetty.hsepermhelper.service.EventService
import org.springframework.stereotype.Service

@Service
class EventServiceImpl(
    private val eventRepository: EventRepository,
): EventService {
    override fun getAllEvents(): List<PublicEvent> {
        return eventRepository.getAllEvents()
    }

    override fun addEvent(event: PublicEvent) {
        eventRepository.addEvent(event)
    }

    override fun deleteFirstEvent(): Boolean {
        return eventRepository.deleteFirstEvent()
    }

    override fun deleteLastEvent(): Boolean {
        return eventRepository.deleteLastEvent()
    }

    override fun clearEvents() {
        return eventRepository.clearEvents()
    }

    override fun deleteEvents(events: List<PublicEvent>) {
        eventRepository.deleteEvents(events)
    }

    override fun deleteEvent(event: PublicEvent) {
        deleteEvents(listOf(event))
    }
}