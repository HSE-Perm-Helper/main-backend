package com.melowetty.hsepermhelper.controller

import com.melowetty.hsepermhelper.annotation.Slf4j
import com.melowetty.hsepermhelper.annotation.Slf4j.Companion.log
import com.melowetty.hsepermhelper.domain.model.ErrorResponse
import com.melowetty.hsepermhelper.exception.CustomException
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.exception.UserIsExistsException
import com.melowetty.hsepermhelper.exception.UserNotFoundException
import com.melowetty.hsepermhelper.exception.verification.VerificationNotFoundOrExpiredException
import jakarta.validation.ConstraintViolationException
import java.lang.Boolean.parseBoolean
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
@Slf4j
class ExceptionHandlerController(
    environment: Environment
) {
    private val isDebug = parseBoolean(environment["debug"])

    @ExceptionHandler(VerificationNotFoundOrExpiredException::class)
    fun handleVerificationNotFoundOrExpiredException(exception: VerificationNotFoundOrExpiredException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .header("Content-Type", "text/plain; charset=utf-8")
            .body(exception.message)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ScheduleNotFoundException::class)
    fun handleScheduleNotFoundException(exception: ScheduleNotFoundException): ResponseEntity<Any> {
        return exceptionToDebugOrNormalResponseEntity(exception)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<Any> {
        return exceptionToDebugOrNormalResponseEntity(
            exception,
            message = "Произошла ошибка во время выполнения запроса!", HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(exception: NoHandlerFoundException): ResponseEntity<Any> {
        return exceptionToDebugOrNormalResponseEntity(
            exception,
            message = "Страница не найдена!", HttpStatus.NOT_FOUND
        )
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exception: UserNotFoundException): ResponseEntity<Any> {
        return exceptionToDebugOrNormalResponseEntity(exception)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(exception: MissingServletRequestParameterException): ResponseEntity<Any> {
        return exceptionToDebugOrNormalResponseEntity(
            exception,
            message = "Необходимый параметр запроса ${exception.parameterName} типа ${exception.parameterType} не передан!",
            statusCode = HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ResponseEntity<Any> {
        return exceptionToDebugOrNormalResponseEntity(
            exception,
            message = "Необходимое тело запроса не передано!", HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(exception: IllegalArgumentException): ResponseEntity<Any> {
        return exceptionToDebugOrNormalResponseEntity(
            exception,
            "Неверный параметр в запросе!", HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(UserIsExistsException::class)
    fun handleUserIsExistsException(exception: UserIsExistsException): ResponseEntity<Any> {
        return exceptionToDebugOrNormalResponseEntity(exception)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<Any> {
        return ResponseEntity(
            ErrorResponse(
            "Ошибка валидации", "VALIDATION_ERROR", status = 400),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<Any> {
        return exceptionToDebugOrNormalResponseEntity(e)
    }

    private fun exceptionToDebugOrNormalResponseEntity(exception: CustomException): ResponseEntity<Any> {
        if (isDebug) {
            log.error(exception.stackTraceToString())
            return exception.toDebugResponseEntity()
        }
        return exception.toResponseEntity()
    }

    private fun exceptionToDebugOrNormalResponseEntity(
        exception: Exception,
        message: String,
        statusCode: HttpStatusCode
    ): ResponseEntity<Any> {
        val response = ErrorResponse(
            message = exception.message ?: message,
            code = exception.javaClass.simpleName,
            status = statusCode.value(),
        )

        if (isDebug) {
            log.error(exception.stackTraceToString())
            return ResponseEntity(response.toDebugResponse(exception), statusCode)
        }
        return ResponseEntity(response, statusCode)
    }
}