package com.melowetty.hsepermhelper.exception.verification

import com.melowetty.hsepermhelper.exception.CustomException
import org.springframework.http.HttpStatus

class ReachMaxAttemptsToVerificationRequestException: CustomException(
    message = "Достигнуто максимальное количество попыток на отправку письма",
    statusCode = HttpStatus.BAD_REQUEST
)