package com.melowetty.hsepermhelper.messaging.event.notification.verification

import com.melowetty.hsepermhelper.messaging.event.notification.NotificationV2
import com.melowetty.hsepermhelper.messaging.event.notification.NotificationType

data class EmailVerificationSendNotification(
    val email: String,
    val link: String
): NotificationV2(
    NotificationType.EMAIL_VERIFICATION
)
