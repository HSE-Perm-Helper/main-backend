package com.melowetty.hsepermhelper.exception.user

import com.melowetty.hsepermhelper.exception.CustomException
import org.springframework.http.HttpStatus

@Deprecated("Make abstract for exception")
open class UserNotFoundException(message: String) : CustomException(message, HttpStatus.NOT_FOUND)