package com.melowetty.hsepermhelper.notification

open class KafkaNotificationV2(
    val notificationType: NotificationType,
    val recipient: NotificationRecipient = NotificationRecipient.NONE
)