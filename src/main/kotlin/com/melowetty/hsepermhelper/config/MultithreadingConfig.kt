package com.melowetty.hsepermhelper.config

import org.slf4j.MDC
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MultithreadingConfig {

    @Bean("check-changes-from-hse-api-executor-service")
    fun executorServiceForHseApiCheckingChanges(): ExecutorService {
        return Executors.newFixedThreadPool(10) { runnable ->
            newThreadWithMdc(runnable, "hse-api-checker")
        }
    }

    @Bean("add-user-events-executor-service")
    fun executorServiceForAddingUserEvents(): ExecutorService {
        return Executors.newFixedThreadPool(10) { runnable ->
            newThreadWithMdc(runnable, "user-events")
        }
    }

    private fun newThreadWithMdc(runnable: Runnable, name: String): Thread {
        val contextMap = MDC.getCopyOfContextMap()

        val thread = Thread(runnable, name)
        thread.setUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
        }
        return object : Thread(runnable, name) {
            override fun run() {
                val previous = MDC.getCopyOfContextMap()
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap)
                    }
                    super.run()
                } finally {
                    MDC.clear()
                    if (previous != null) {
                        MDC.setContextMap(previous)
                    }
                }
            }
        }
    }
}