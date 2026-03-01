package com.melowetty.hsepermhelper.domain.model.event

data class KafkaNotification(
    val notificationType: String,
    val payload: Any
)
