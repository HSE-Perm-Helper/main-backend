package com.melowetty.hsepermhelper.events.common

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

open class PublicEvent {
    @JsonProperty("createdTime")
    @Schema(description = "Дата создания ивента")
    val date = LocalDateTime.now()
    @JsonProperty("eventType")
    @Schema(description = "Тип ивента")
    fun getEventType(): String {
        return javaClass.simpleName.split(Regex("(?=\\p{Upper})")).joinToString("_")
    }
}