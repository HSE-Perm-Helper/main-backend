package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.events.PublicEvent

interface EventRepository {
    /**
     * Get all events as list
     *
     * @return list of events
     */
    fun getAllEvents(): List<PublicEvent>

    /**
     * Add event
     *
     * @param event
     */
    fun addEvent(event: PublicEvent)

    /**
     * Delete first event
     * Returns true if event is deleted or false if event is not deleted
     *
     * @return if event is deleted true or false
     */
    fun deleteFirstEvent(): Boolean

    /**
     * Delete last event
     * Returns true if event is deleted or false if event is not deleted
     *
     * @return if event is deleted true or false
     */
    fun deleteLastEvent(): Boolean

    /**
     * Clears all events
     *
     */
    fun clearEvents()
}