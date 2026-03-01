package com.melowetty.hsepermhelper.exception.user

class UserByTelegramIdNotFoundException(
    val telegramId: Long
) : UserNotFoundException("User with telegramId $telegramId not found")