package com.melowetty.hsepermhelper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
class HsePermHelperApplication

fun main(args: Array<String>) {
    runApplication<HsePermHelperApplication>(*args)
}
