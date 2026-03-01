package com.melowetty.hsepermhelper.remote.exception

import com.melowetty.hsepermhelper.exception.CustomException
import org.springframework.http.HttpStatus

class CalendarTokenNotFoundException: CustomException(
    message = "Calendar token not found",
    statusCode = HttpStatus.NOT_FOUND
)