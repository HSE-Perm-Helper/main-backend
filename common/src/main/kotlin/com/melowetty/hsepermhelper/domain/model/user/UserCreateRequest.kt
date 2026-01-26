package com.melowetty.hsepermhelper.domain.model.user

data class UserCreateRequest(
    val telegramId: Long,
    val group: String
)
