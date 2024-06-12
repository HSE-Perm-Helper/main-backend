package com.melowetty.hsepermhelper.exception

import org.springframework.http.HttpStatus

class UnauthorizedException(message: String): CustomException(message, HttpStatus.UNAUTHORIZED)