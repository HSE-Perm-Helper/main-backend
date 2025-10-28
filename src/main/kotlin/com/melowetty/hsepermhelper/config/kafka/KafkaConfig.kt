package com.melowetty.hsepermhelper.config.kafka

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka

@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaTopicsConfig::class)
@ConditionalOnProperty("app.message-broker.type", havingValue = "kafka")
class KafkaConfig