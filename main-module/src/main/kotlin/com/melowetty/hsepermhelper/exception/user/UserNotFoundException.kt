package com.melowetty.hsepermhelper.exception.user

import com.melowetty.hsepermhelper.exception.CustomException
import org.springframework.http.HttpStatus

abstract class UserNotFoundException(message: String) : CustomException(message, HttpStatus.NOT_FOUND)