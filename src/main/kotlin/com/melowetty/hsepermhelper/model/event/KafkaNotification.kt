package com.melowetty.hsepermhelper.model.event

data class KafkaNotification(
    val notificationType: String,
    val payload: Any
)
