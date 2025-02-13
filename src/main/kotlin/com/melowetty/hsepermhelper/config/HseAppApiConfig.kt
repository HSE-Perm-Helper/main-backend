package com.melowetty.hsepermhelper.config

import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.support.RetryTemplate
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate

@Configuration
class HseAppApiConfig {
    @Value("\${api.hse-app-x.base-url}")
    private lateinit var baseUrl: String

    @Bean("hse-app")
    fun hseAppRestTemplate(): RestTemplate {
        return RestTemplateBuilder()
            .defaultHeader("User-Agent", "Hse Perm Helper@1.0.0")
            .defaultHeader("Accept-Language", "ru-RU, ru;q=0.9, en-US;q=0.8, en;q=0.7")
            .rootUri(baseUrl)
            .build()
    }

    @Bean("hse-app-retryer")
    fun hseAppRetryTemplate(): RetryTemplate {
        return RetryTemplate.builder()
            .retryOn(HttpServerErrorException::class.java)
            .maxAttempts(3)
            .fixedBackoff(Duration.ofMillis(300))
            .build()
    }
}