package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.event.UserEventType

interface UserEventService {
    /**
     * Add user event by telegram Id
     *
     * @param telegramId target user telegram id
     * @param eventType type of event
     */
    fun addUserEvent(telegramId: Long, eventType: UserEventType)
}