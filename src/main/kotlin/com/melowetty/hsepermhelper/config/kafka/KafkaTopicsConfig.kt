package com.melowetty.hsepermhelper.config.kafka

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.message-broker.kafka.topics")
data class KafkaTopicsConfig(
    val baseNotifications: String,
    val newNotifications: String,
    val userEvents: String,
)
