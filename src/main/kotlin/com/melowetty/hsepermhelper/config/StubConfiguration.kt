package com.melowetty.hsepermhelper.config

import com.melowetty.hsepermhelper.messaging.broker.MessageBrokerService
import com.melowetty.hsepermhelper.messaging.broker.StubMessageBrokerService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StubConfiguration {
    @Bean
    @ConditionalOnMissingBean(MessageBrokerService::class)
    fun stubMessageBrokerService(): MessageBrokerService {
        logger.info { "Used stub message broker service" }
        return StubMessageBrokerService()
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}