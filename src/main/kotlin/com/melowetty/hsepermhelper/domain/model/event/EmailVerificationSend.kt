package com.melowetty.hsepermhelper.domain.model.event

data class EmailVerificationSend(
    val email: String,
    val link: String
): KafkaNotificationV2(
    "EMAIL_VERIFICATION"
)
