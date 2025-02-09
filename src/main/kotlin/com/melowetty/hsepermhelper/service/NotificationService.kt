package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.event.KafkaNotificationV2
import com.melowetty.hsepermhelper.notification.Notification

interface NotificationService {
    /**
     * Send notification
     *
     * @param notification
     */
    fun sendNotification(notification: Notification)

    fun sendNotificationV2(notification: KafkaNotificationV2)
}
