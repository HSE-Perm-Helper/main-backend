package com.melowetty.hsepermhelper.exception

import org.springframework.http.HttpStatus

class ScheduleNotFoundException(message: String) : CustomException(message, HttpStatus.NOT_FOUND)