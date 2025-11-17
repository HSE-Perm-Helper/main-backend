package com.melowetty.hsepermhelper.util

import org.slf4j.MDC

object LoggingUtils {
    private const val LOGGING_REQUEST_ID_KEY = "request_id"

    fun executeWithRequestIdContext(requestId: String, block: () -> Unit) {
        try {
            MDC.put(LOGGING_REQUEST_ID_KEY, requestId)
            return block()
        } finally {
            MDC.clear()
        }
    }

    fun executeWithRequestIdContext(block: () -> Unit) {
        val requestId = RequestIdGenerator.generate()
        try {
            MDC.put(LOGGING_REQUEST_ID_KEY, requestId)
            return block()
        } finally {
            MDC.clear()
        }
    }

    fun executeWithContext(params: Map<String, Any>, block: () -> Unit) {
        try {
            params.forEach { (key, value) -> MDC.put(key, value.toString()) }
            return block()
        } finally {
            params.forEach { (key, _) -> MDC.remove(key) }
        }
    }
}