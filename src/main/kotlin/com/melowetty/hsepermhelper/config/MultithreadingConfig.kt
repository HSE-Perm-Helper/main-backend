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
        return Executors.newFixedThreadPool(10)
    }

    @Bean("add-user-events-executor-service")
    fun executorServiceForAddingUserEvents(): ExecutorService {
        return Executors.newFixedThreadPool(10) { runnable ->
            Thread {
                val context = MDC.getCopyOfContextMap()
                try {
                    if (context != null) MDC.setContextMap(context)
                    runnable.run()
                } finally {
                    MDC.clear()
                }
            }
        }
    }
}