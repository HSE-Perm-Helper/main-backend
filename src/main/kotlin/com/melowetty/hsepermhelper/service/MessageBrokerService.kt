package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.notification.NotificationV2
import com.melowetty.hsepermhelper.notification.Notification
import java.util.UUID

interface MessageBrokerService {
    fun sendUserEvent(userId: UUID, eventType: UserEventType)
    fun sendNotificationV1(notification: Notification)
    fun sendNotificationV2(userId: UUID?, notification: NotificationV2)
    fun sendBatchNotificationsV2(userIds: List<UUID>, notification: NotificationV2)
}