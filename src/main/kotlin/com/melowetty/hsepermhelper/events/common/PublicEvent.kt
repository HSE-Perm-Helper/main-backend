package com.melowetty.hsepermhelper.events.common

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

abstract class PublicEvent {
    @JsonProperty("createdTime")
    @Schema(description = "Дата создания ивента")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    val date = LocalDateTime.now()
    @JsonProperty("eventType")
    @Schema(description = "Тип ивента")
    fun getEventType(): String {
        return javaClass.simpleName
            .split(Regex("(?=\\p{Upper})"))
            .filter { it.isNotEmpty() }
            .joinToString("_") { it.uppercase() }
    }
}