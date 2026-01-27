package com.melowetty.hsepermhelper.messaging.broker

import com.melowetty.hsepermhelper.messaging.event.notification.Notification
import com.melowetty.hsepermhelper.messaging.event.notification.NotificationV2
import com.melowetty.hsepermhelper.messaging.event.task.ChangeDetectionTask
import com.melowetty.hsepermhelper.messaging.event.task.NewTimetableNotifyTask
import java.util.*

interface MessageBrokerService {
    fun submitTimetableChangeDetection(task: ChangeDetectionTask)
    fun submitNewTimetableNotifyTask(task: NewTimetableNotifyTask)
    fun sendNotificationV1(notification: Notification)
    fun sendNotificationV2(userId: UUID?, notification: NotificationV2)
    fun sendBatchNotificationsV2(userIds: List<UUID>, notification: NotificationV2)
}