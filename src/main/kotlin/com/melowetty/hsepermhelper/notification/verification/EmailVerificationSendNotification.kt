package com.melowetty.hsepermhelper.notification.verification

import com.melowetty.hsepermhelper.notification.KafkaNotificationV2
import com.melowetty.hsepermhelper.notification.NotificationType

data class EmailVerificationSendNotification(
    val email: String,
    val link: String
): KafkaNotificationV2(
    NotificationType.EMAIL_VERIFICATION
)
