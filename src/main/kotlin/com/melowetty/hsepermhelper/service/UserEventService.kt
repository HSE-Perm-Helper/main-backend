package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.dto.UserDto
import com.melowetty.hsepermhelper.dto.UserEventDto
import com.melowetty.hsepermhelper.models.UserEventType

interface UserEventService {
    /**
     * Add user event by telegram Id
     *
     * @param telegramId target user telegram id
     * @param eventType type of event
     */
    fun addUserEvent(telegramId: Long, eventType: UserEventType)

    /**
     * Add user event to db
     *
     * @param user target user
     * @param eventType type of event
     */
    fun addUserEvent(user: UserDto, eventType: UserEventType)

    /**
     * Get all user events by user
     *
     * @param user target user
     * @return list of user events
     */
    fun getAllUserEvents(user: UserDto): List<UserEventDto>

    /**
     * Get all user events by user and filtered by event type
     *
     * @param user target user
     * @param eventType target event type
     * @return filtered list of user events
     */
    fun getAllUserEvents(user: UserDto, eventType: UserEventType): List<UserEventDto>
}