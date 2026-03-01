package com.melowetty.hsepermhelper.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.retry.RetryPolicy
import org.springframework.core.retry.RetryTemplate
import org.springframework.util.backoff.FixedBackOff
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient

@Configuration
class HseAppApiConfig {
    @Value("\${api.hse-app-x.base-url}")
    private lateinit var baseUrl: String

    @Bean("hse-app")
    fun hseAppRestTemplate(): RestClient {
        return RestClient.builder()
            .defaultHeader("User-Agent", "Hse Perm Helper@1.0.0")
            .defaultHeader("Accept-Language", "ru-RU, ru;q=0.9, en-US;q=0.8, en;q=0.7")
            .baseUrl(baseUrl)
            .build()
    }

    @Bean("hse-app-retryer")
    fun hseAppRetryTemplate(): RetryTemplate {
        val retryPolicy = RetryPolicy.builder()
            .backOff(FixedBackOff(300L, 3))
            .predicate {
                it is HttpServerErrorException
            }
            .build()

        return RetryTemplate(retryPolicy)
    }
}