package com.melowetty.hsepermhelper.notification

import com.melowetty.hsepermhelper.domain.model.event.KafkaNotificationV2

data class EmailIsVerifiedNotification(
    val telegramId: Long
): KafkaNotificationV2("EMAIL_IS_VERIFIED")
