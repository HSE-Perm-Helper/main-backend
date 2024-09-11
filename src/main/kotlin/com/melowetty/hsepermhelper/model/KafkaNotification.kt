package com.melowetty.hsepermhelper.model

data class KafkaNotification(
    val notificationType: String,
    val payload: Any
)
