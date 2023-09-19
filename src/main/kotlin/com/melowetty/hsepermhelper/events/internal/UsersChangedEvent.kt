package com.melowetty.hsepermhelper.events.internal

import com.melowetty.hsepermhelper.dto.UserDto
import com.melowetty.hsepermhelper.events.common.CustomEvent
import com.melowetty.hsepermhelper.events.common.EventType

class UsersChangedEvent(
    user: UserDto,
    type: EventType
): CustomEvent<UserDto>(user, type)