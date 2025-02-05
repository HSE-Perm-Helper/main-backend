package com.melowetty.hsepermhelper.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka

@Configuration
@Profile("!dev")
@EnableKafka
class KafkaConfig {
}