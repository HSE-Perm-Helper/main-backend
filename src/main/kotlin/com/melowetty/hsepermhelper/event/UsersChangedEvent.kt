package com.melowetty.hsepermhelper.event

import com.melowetty.hsepermhelper.domain.dto.UserDto

class UsersChangedEvent(
    user: UserDto,
    type: EventType
) : CustomEvent<UserDto>(user, type)