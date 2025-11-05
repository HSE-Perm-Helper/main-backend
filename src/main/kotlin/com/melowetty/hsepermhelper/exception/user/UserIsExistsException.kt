package com.melowetty.hsepermhelper.exception.user

import com.melowetty.hsepermhelper.exception.CustomException
import org.springframework.http.HttpStatus

class UserIsExistsException(message: String) : CustomException(message, HttpStatus.CONFLICT)