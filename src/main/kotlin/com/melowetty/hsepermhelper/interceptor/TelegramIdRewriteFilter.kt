package com.melowetty.hsepermhelper.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import com.melowetty.hsepermhelper.exception.user.UserByTelegramIdNotFoundException
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class TelegramIdRewriteFilter(
    private val userStorage: UserStorage,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

    private val telegramIdPattern = Regex("""/v3/users/(\d{5,})(/.*)?""")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        val matchResult = telegramIdPattern.find(path)
        if (matchResult != null) {
            val telegramId = matchResult.groupValues[1].toLongOrNull()

            if (telegramId != null) {
                val id = userStorage.getUserIdByTelegramId(telegramId)

                if (id != null) {
                    val newPath = path.replace(
                        "/v3/users/$telegramId",
                        "/v3/users/${id}"
                    )

                    val wrappedRequest = PathRewriteRequestWrapper(request, newPath)
                    filterChain.doFilter(wrappedRequest, response)
                    return
                } else {
                    val error = UserByTelegramIdNotFoundException(telegramId).toResponseEntity()
                    writeResponseEntity(response, error)
                    return
                }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun writeResponseEntity(
        response: HttpServletResponse,
        responseEntity: ResponseEntity<*>
    ) {
        response.status = responseEntity.statusCode.value()
        responseEntity.headers.forEach { (key, values) ->
            values.forEach { value ->
                response.addHeader(key, value)
            }
        }

        if (responseEntity.body != null) {
            response.contentType = "application/json"
            objectMapper.writeValue(response.writer, responseEntity.body)
        }
    }

    class PathRewriteRequestWrapper(
        request: HttpServletRequest,
        private val newPath: String
    ) : HttpServletRequestWrapper(request) {

        override fun getRequestURI(): String = newPath

        override fun getServletPath(): String = newPath
    }
}