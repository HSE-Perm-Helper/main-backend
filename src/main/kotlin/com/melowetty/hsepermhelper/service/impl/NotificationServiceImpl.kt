package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.controllers.request.NotificationData
import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.repository.NotificationRepository
import com.melowetty.hsepermhelper.service.NotificationService
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
): NotificationService {
    override fun getAllNotifications(): List<Notification> {
        return notificationRepository.getAllNotifications()
    }

    override fun addNotification(notification: Notification) {
        notificationRepository.addNotification(notification)
    }

    override fun clearNotifications() {
        return notificationRepository.clearNotifications()
    }

    override fun deleteNotifications(notifications: List<NotificationData>) {
        notifications.forEach {
            notificationRepository.deleteNotificationById(it.id)
        }
    }
}