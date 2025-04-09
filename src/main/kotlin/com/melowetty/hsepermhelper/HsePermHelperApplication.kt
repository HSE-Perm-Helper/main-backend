package com.melowetty.hsepermhelper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
@EnableScheduling
@EnableFeignClients
class HsePermHelperApplication

fun main(args: Array<String>) {
    runApplication<HsePermHelperApplication>(*args)
}
