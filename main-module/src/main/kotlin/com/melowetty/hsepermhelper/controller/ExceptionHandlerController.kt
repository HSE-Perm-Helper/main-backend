package com.melowetty.hsepermhelper.controller

import com.melowetty.hsepermhelper.domain.model.ErrorResponse
import com.melowetty.hsepermhelper.exception.CustomException
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.exception.user.UserIsExistsException
import com.melowetty.hsepermhelper.exception.user.UserNotFoundException
import com.melowetty.hsepermhelper.exception.verification.VerificationNotFoundOrExpiredException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class ExceptionHandlerController {
    @ExceptionHandler(VerificationNotFoundOrExpiredException::class)
    fun handleVerificationNotFoundOrExpiredException(exception: VerificationNotFoundOrExpiredException): ResponseEntity<String> {
        log.warn(exception) {
            "Verification not found or expired"
        }
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .header("Content-Type", "text/plain; charset=utf-8")
            .body(exception.message)
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ErrorResponse> {
        log.warn(e) {
            e.message
        }
        return e.toResponseEntity()
    }

    @ExceptionHandler
    fun handleMissingServletRequestParameterException(exception: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        log.warn(exception) {
            "Parameter is missing"
        }
        return exceptionToEntity(
            exception,
            message = "Необходимый параметр запроса ${exception.parameterName} типа ${exception.parameterType} не передан!",
            statusCode = HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        log.warn(exception) {
            "Http message not readable"
        }
        return exceptionToEntity(
            exception,
            message = "Необходимое тело запроса не передано!",
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<Any> {
        log.warn(e) {
            "Validation error"
        }
        return ResponseEntity(
            ErrorResponse(
            "Ошибка валидации", "VALIDATION_ERROR", status = 400),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(exception: NoHandlerFoundException): ResponseEntity<ErrorResponse> {
        log.warn(exception) {
            "Handler not found"
        }
        return exceptionToEntity(
            exception,
            message = "Страница не найдена!",
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler
    fun handleIllegalArgumentException(exception: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        log.warn(exception) {
            "Illegal argument exception"
        }
        return exceptionToEntity(
            exception,
            "Неверный параметр в запросе!",
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        log.warn(exception) {
            exception.message
        }
        return exceptionToEntity(
            exception,
            message = "Произошла ошибка во время выполнения запроса!",
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    private fun exceptionToEntity(
        exception: Exception,
        message: String,
        statusCode: HttpStatusCode
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = exception.message ?: message,
            code = exception.javaClass.simpleName,
            status = statusCode.value(),
        )

        return ResponseEntity(response, statusCode)
    }

    companion object {
        private val log = KotlinLogging.logger {  }
    }
}