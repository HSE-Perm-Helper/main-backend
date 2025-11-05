package com.melowetty.hsepermhelper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

@SpringBootApplication
@EnableWebMvc
@EnableScheduling
@EnableFeignClients
class HsePermHelperApplication

fun main(args: Array<String>) {
    val instanceId = generateInstanceId()
    System.setProperty("app.instance-id", instanceId)
    runApplication<HsePermHelperApplication>(*args)
}

private fun generateInstanceId(): String {
    val timePart = (System.currentTimeMillis() % 1679616).toString(36)
    val randomPart = (ThreadLocalRandom.current().nextLong(1679616)).toString(36)

    return (timePart.padStart(4, '0') + randomPart.padStart(4, '0'))
        .take(8)
}
