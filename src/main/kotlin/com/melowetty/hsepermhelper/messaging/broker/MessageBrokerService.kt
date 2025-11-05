package com.melowetty.hsepermhelper.messaging.broker

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.messaging.event.notification.NotificationV2
import com.melowetty.hsepermhelper.messaging.event.notification.Notification
import com.melowetty.hsepermhelper.messaging.event.task.ChangeDetectionTask
import com.melowetty.hsepermhelper.messaging.event.task.NewTimetableNotifyTask
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import java.util.UUID

interface MessageBrokerService {
    fun submitTimetableChangeDetection(task: ChangeDetectionTask)
    fun submitNewTimetableNotifyTask(task: NewTimetableNotifyTask)
    fun sendUserEvent(userId: UUID, eventType: UserEventType)
    fun sendNotificationV1(notification: Notification)
    fun sendNotificationV2(userId: UUID?, notification: NotificationV2)
    fun sendBatchNotificationsV2(userIds: List<UUID>, notification: NotificationV2)
}