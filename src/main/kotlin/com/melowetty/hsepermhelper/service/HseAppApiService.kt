package com.melowetty.hsepermhelper.service

import java.time.LocalDate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class HseAppApiService(
    @Qualifier("hse-app")
    private val restTemplate: RestTemplate
) {
    fun getLessons(tudentEmail: String, from: LocalDate, to: LocalDate)
}