package com.melowetty.hsepermhelper.util

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom

@Component
class InstanceIdGenerator {
    @Bean
    fun instanceId(): String {
        val random = ThreadLocalRandom.current().nextLong(2_821_109_907_456) // 36^8
        return random.toString(36).padStart(8, '0')
    }
}