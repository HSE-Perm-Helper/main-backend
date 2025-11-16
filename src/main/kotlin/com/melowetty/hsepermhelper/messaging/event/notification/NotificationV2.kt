package com.melowetty.hsepermhelper.messaging.event.notification

import com.fasterxml.jackson.annotation.JsonIgnore

open class NotificationV2(
    val notificationType: NotificationType,

    @JsonIgnore
    val recipient: NotificationRecipient = NotificationRecipient.NONE
)