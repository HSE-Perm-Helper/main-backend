package com.melowetty.hsepermhelper.config.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaTopicsConfig::class)
@ConditionalOnProperty("app.message-broker.type", havingValue = "kafka")
class KafkaConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean
    fun consumerConfigs(): Map<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        return props
    }

    @Bean
    fun consumerFactoryHashMap(): ConsumerFactory<String, HashMap<String, Any?>> =
        DefaultKafkaConsumerFactory(
            consumerConfigs(),
            StringDeserializer(),
            JsonDeserializer(Map::class.java, false),
        )

    @Bean
    fun kafkaListenerContainerFactoryHashMap(): ConcurrentKafkaListenerContainerFactory<String, HashMap<String, Any?>> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, HashMap<String, Any?>> =
            ConcurrentKafkaListenerContainerFactory<String, HashMap<String, Any?>>()
        factory.consumerFactory = consumerFactoryHashMap()
        return factory
    }
}