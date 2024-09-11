package com.melowetty.hsepermhelper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@ServletComponentScan
@EnableWebMvc
@EnableScheduling
@EnableDiscoveryClient
@EnableKafka
class HsePermHelperApplication

fun main(args: Array<String>) {
    runApplication<HsePermHelperApplication>(*args)
}
