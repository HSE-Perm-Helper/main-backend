package com.melowetty.hsepermhelper.exception

import org.springframework.http.HttpStatus

class UserIsExistsException(message: String): CustomException(message, HttpStatus.CONFLICT)