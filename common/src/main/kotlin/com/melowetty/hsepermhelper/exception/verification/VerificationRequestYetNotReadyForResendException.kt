package com.melowetty.hsepermhelper.exception.verification

import com.melowetty.hsepermhelper.exception.CustomException
import org.springframework.http.HttpStatus

class VerificationRequestYetNotReadyForResendException: CustomException(
    message = "Запрос на верификацию пока нельзя переотправить, необходимо немного подождать",
    statusCode = HttpStatus.BAD_REQUEST
)