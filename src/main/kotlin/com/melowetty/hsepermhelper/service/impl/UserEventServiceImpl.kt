package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.dto.UserDto
import com.melowetty.hsepermhelper.dto.UserEventDto
import com.melowetty.hsepermhelper.models.UserEventType
import com.melowetty.hsepermhelper.service.UserEventService

class UserEventServiceImpl: UserEventService {
    override fun addUserEvent(user: UserDto, eventType: UserEventType) {
        TODO("Not yet implemented")
    }

    override fun getAllUserEvents(user: UserDto): List<UserEventDto> {
        TODO("Not yet implemented")
    }

    override fun getAllUserEvents(user: UserDto, eventType: UserEventType): List<UserEventDto> {
        TODO("Not yet implemented")
    }
}