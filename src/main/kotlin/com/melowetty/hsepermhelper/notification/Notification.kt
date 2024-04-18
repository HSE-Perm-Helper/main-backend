package com.melowetty.hsepermhelper.notification

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

abstract class Notification {
    val id: UUID = UUID.randomUUID()
    @JsonProperty("createdTime")
    @Schema(description = "Дата создания уведомления")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    val date = LocalDateTime.now()

    @JsonProperty("notificationType")
    @Schema(description = "Тип уведомления")
    abstract fun getNotificationType(): String
}