package com.melowetty.hsepermhelper.notification.verification

import com.melowetty.hsepermhelper.notification.NotificationV2
import com.melowetty.hsepermhelper.notification.NotificationType

data class EmailVerificationSendNotification(
    val email: String,
    val link: String
): NotificationV2(
    NotificationType.EMAIL_VERIFICATION
)
