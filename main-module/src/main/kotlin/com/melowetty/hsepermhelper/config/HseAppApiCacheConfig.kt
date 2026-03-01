package com.melowetty.hsepermhelper.config

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableCaching
class HseAppApiCacheConfig {
    companion object {
        const val HSE_APP_LESSONS_CACHE = "hse-app-lessons"
    }

    @Bean
    fun caffeineConfig(): Caffeine<Any, Any> {
        return Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES)
    }

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>): CacheManager? {
        val caffeineCacheManager = CaffeineCacheManager()
        caffeineCacheManager.getCache(HSE_APP_LESSONS_CACHE)
        caffeineCacheManager.setCaffeine(caffeine)
        return caffeineCacheManager
    }
}