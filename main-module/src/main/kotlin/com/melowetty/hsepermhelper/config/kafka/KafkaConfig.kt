package com.melowetty.hsepermhelper.config.kafka

import tools.jackson.databind.json.JsonMapper
import kotlin.jvm.java
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonSerializer


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
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JacksonJsonDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        return props
    }

    @Bean
    fun consumerFactoryHashMap(): ConsumerFactory<String, HashMap<String, Any?>> =
        DefaultKafkaConsumerFactory(
            consumerConfigs(),
            StringDeserializer(),
            JacksonJsonDeserializer(Map::class.java, false),
        )

    @Bean
    fun kafkaListenerContainerFactoryHashMap(): ConcurrentKafkaListenerContainerFactory<String, HashMap<String, Any?>> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, HashMap<String, Any?>> =
            ConcurrentKafkaListenerContainerFactory()
        factory.setConsumerFactory(consumerFactoryHashMap())
        return factory
    }

    @Bean
    fun producerConfigs(): MutableMap<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JacksonJsonSerializer::class.java
        return props
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> = DefaultKafkaProducerFactory<String, Any>(
        producerConfigs(),
        StringSerializer(),
        JacksonJsonSerializer<Any>(JsonMapper()),
    )

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory())
    }
}