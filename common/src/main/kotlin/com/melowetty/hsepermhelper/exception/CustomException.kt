package com.melowetty.hsepermhelper.exception

import com.melowetty.hsepermhelper.domain.model.ErrorResponse
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity

abstract class CustomException(
    override val message: String,
    private val statusCode: HttpStatusCode
) : RuntimeException(message) {
    fun toResponseEntity(): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = message,
            code = javaClass.simpleName,
            status = statusCode.value()
        )
        return ResponseEntity(response, statusCode)
    }
}