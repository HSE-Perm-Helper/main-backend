package com.melowetty.hsepermhelper.service;

import com.melowetty.hsepermhelper.controllers.request.NotificationData
import com.melowetty.hsepermhelper.notification.Notification

interface NotificationService {
    /**
     * Get all notifications as list
     *
     * @return list of notifications
     */
    fun getAllNotifications(): List<Notification>

    /**
     * Add notification
     *
     * @param notification
     */
    fun addNotification(notification: Notification)

    /**
     * Clears all notifications
     *
     */
    fun clearNotifications()

    /**
     * Delete specific notifications
     *
     * @param notifications list of notifications for deleting
     */
    fun deleteNotifications(notifications: List<NotificationData>)
}
