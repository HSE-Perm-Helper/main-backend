package com.melowetty.hsepermhelper.events

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

open class PublicEvent(
    val id: Long,
) {
    @JsonProperty("createdTime")
    val date = LocalDateTime.now()
    @JsonProperty("eventType")
    fun getEventType(): String {
        return javaClass.simpleName.split(Regex("(?=\\p{Upper})")).joinToString("_")
    }
}