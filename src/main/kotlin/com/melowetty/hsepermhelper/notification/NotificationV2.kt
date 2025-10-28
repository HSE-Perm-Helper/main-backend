package com.melowetty.hsepermhelper.notification

open class NotificationV2(
    val notificationType: NotificationType,
    val recipient: NotificationRecipient = NotificationRecipient.NONE
)