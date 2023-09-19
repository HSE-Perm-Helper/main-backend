package com.melowetty.hsepermhelper.service;

import com.melowetty.hsepermhelper.events.PublicEvent

public interface EventService {
    /**
     * Get all events as list
     *
     * @return list of events
     */
    fun getAllEvents(): List<PublicEvent>

    /**
     * Delete event by id
     * Returns true if event is deleted or false if event is not deleted
     *
     * @param id event id
     * @return if event is deleted true or false
     */
    fun deleteEvent(id: Long): Boolean

    /**
     * Delete some events by list of ids
     * Returns false if any event is not deleted
     *
     * @param ids list of event ids
     * @return return true if all events is deleted or false
     */
    fun deleteEvents(ids: List<Long>): Boolean

    /**
     * Clears all events
     *
     */
    fun clearEvents()
}
