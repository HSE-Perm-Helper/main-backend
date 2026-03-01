package com.melowetty.hsepermhelper.exception.verification

import com.melowetty.hsepermhelper.exception.CustomException
import org.springframework.http.HttpStatus

class VerificationRequestNotFoundException: CustomException(
    "Верификация с таким ID не найдена",
    HttpStatus.NOT_FOUND
)