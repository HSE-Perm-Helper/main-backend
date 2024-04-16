package com.melowetty.hsepermhelper.repository.impl

import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.repository.NotificationRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class NotificationRepositoryImpl: NotificationRepository {
    private val notifications = mutableListOf<Notification>()
    override fun getAllNotifications(): List<Notification> {
        return notifications.toList()
    }

    override fun addNotification(notification: Notification) {
        notifications.add(notification)
    }

    override fun clearNotifications() {
        notifications.clear()
    }

    override fun deleteNotificationByHashcode(hashcode: Int) {
        notifications.removeIf { it.hashCode() == hashcode }
    }

    override fun deleteNotificationById(id: UUID) {
        notifications.removeIf { it.id == id }
    }
}