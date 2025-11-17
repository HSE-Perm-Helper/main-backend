package com.melowetty.hsepermhelper.config

import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
        val executor = ThreadPoolTaskExecutor();
        executor.setTaskDecorator(MDCTaskDecorator());
        executor.initialize();
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