package com.melowetty.hsepermhelper.exception.user

import java.util.UUID

class UserByIdNotFoundException(
    val id: UUID,
) : UserNotFoundException("User with id $id not found")