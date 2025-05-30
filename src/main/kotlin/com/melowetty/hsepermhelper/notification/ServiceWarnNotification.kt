package com.melowetty.hsepermhelper.notification

data class ServiceWarnNotification(
    val message: String
): KafkaNotificationV2(
    NotificationType.SERVICE_WARNING,
    NotificationRecipient.SERVICE_ADMIN
)