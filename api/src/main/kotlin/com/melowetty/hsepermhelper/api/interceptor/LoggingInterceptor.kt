package com.melowetty.hsepermhelper.api.interceptor

import com.melowetty.hsepermhelper.util.LoggingUtils
import com.melowetty.hsepermhelper.util.RequestIdGenerator
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class LoggingInterceptor: OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val id = request.getHeader(REQUEST_ID_HEADER) ?: RequestIdGenerator.generate()
        response.addHeader(REQUEST_ID_HEADER, id)

        LoggingUtils.executeWithRequestIdContext(id) {
            filterChain.doFilter(request, response)
        }
    }

    companion object {
        private const val REQUEST_ID_HEADER = "X-Request-ID"
    }
}