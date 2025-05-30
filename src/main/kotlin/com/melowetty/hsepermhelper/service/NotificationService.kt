package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.notification.KafkaNotificationV2
import com.melowetty.hsepermhelper.notification.Notification
import java.util.UUID

interface NotificationService {
    /**
     * Send notification
     *
     * @param notification
     */
    fun sendNotification(notification: Notification)

    fun sendNotificationV2(notification: KafkaNotificationV2)

    fun sendUserNotification(userId: UUID, notification: KafkaNotificationV2)
}
