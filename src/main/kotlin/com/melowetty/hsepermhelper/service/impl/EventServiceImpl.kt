package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.events.PublicEvent
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

    override fun deleteEvent(id: Long): Boolean {
        return eventRepository.deleteEvent(id)
    }

    override fun deleteEvents(ids: List<Long>): Boolean {
        return eventRepository.deleteEvents(ids)
    }

    override fun clearEvents() {
        return eventRepository.clearEvents()
    }

}