package com.melowetty.hsepermhelper.config

import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.service.UserEventService
import com.melowetty.hsepermhelper.service.impl.event.StubUserEventService
import com.melowetty.hsepermhelper.service.impl.notification.StubNotificationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StubConfiguration {
    @Bean
    @ConditionalOnMissingBean(NotificationService::class)
    fun stubNotificationService(): NotificationService {
        logger.info { "Used stub notification service" }
        return StubNotificationService()
    }

    @Bean
    @ConditionalOnMissingBean(UserEventService::class)
    fun stubUserEventService(): UserEventService {
        logger.info { "Used stub user event service" }
        return StubUserEventService()
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}