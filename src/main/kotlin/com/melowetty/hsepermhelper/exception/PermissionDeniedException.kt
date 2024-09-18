package com.melowetty.hsepermhelper.exception

import org.springframework.http.HttpStatus

class PermissionDeniedException(message: String) : CustomException(message, HttpStatus.FORBIDDEN)