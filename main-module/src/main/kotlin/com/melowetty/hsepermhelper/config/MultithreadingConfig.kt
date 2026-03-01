package com.melowetty.hsepermhelper.config

import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ExecutorService

@Configuration
class MultithreadingConfig {

    @Bean("check-changes-from-hse-api-executor-service")
    fun executorServiceForHseApiCheckingChanges(): ExecutorService {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10
        executor.setTaskDecorator(MDCTaskDecorator())
        executor.setThreadNamePrefix("hse-app-changes-")
        executor.initialize()
        return executor.threadPoolExecutor
    }

    @Bean("add-user-events-executor-service")
    fun executorServiceForAddingUserEvents(): ExecutorService {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10
        executor.maxPoolSize = 50
        executor.setTaskDecorator(MDCTaskDecorator())
        executor.setThreadNamePrefix("user-events-")
        executor.initialize()
        return executor.threadPoolExecutor
    }

    class MDCTaskDecorator : TaskDecorator {
        override fun decorate(runnable: Runnable): Runnable {
            val contextMap = MDC.getCopyOfContextMap();
            return Runnable {
                try {
                    MDC.setContextMap(contextMap);
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            }
        }
    }
}