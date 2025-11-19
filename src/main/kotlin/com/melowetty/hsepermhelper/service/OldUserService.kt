package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.UserDto

@Deprecated("Replace by UserService")
interface OldUserService {
    /**
     * Returns list of all users
     *
     * @return list of users
     */
    fun getAllUsers(): List<UserDto>
}