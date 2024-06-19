package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.notification.Notification
import java.util.*

interface NotificationRepository {
    /**
     * Get all Notifications as list
     *
     * @return list of Notifications
     */
    fun getAllNotifications(): List<Notification>

    /**
     * Add Notification
     *
     * @param notification
     */
    fun addNotification(notification: Notification)

    /**
     * Clears all Notifications
     *
     */
    fun clearNotifications()

    /**
     * Delete specific Notification
     *
     * @param id of Notification for deleting
     */
    fun deleteNotificationById(id: UUID)
}