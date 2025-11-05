package com.melowetty.hsepermhelper.messaging.event.notification

open class NotificationV2(
    val notificationType: NotificationType,
    val recipient: NotificationRecipient = NotificationRecipient.NONE
)