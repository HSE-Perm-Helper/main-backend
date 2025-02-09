package com.melowetty.hsepermhelper.exception.verification

import com.melowetty.hsepermhelper.exception.CustomException
import org.springframework.http.HttpStatus

class VerificationNotFoundOrExpiredException: CustomException(
    "Верификация не найдена или истекла",
    HttpStatus.BAD_REQUEST
)