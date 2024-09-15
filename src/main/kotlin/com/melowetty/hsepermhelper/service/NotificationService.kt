package com.melowetty.hsepermhelper.service;

import com.melowetty.hsepermhelper.notification.Notification

interface NotificationService {
    /**
     * Send notification
     *
     * @param notification
     */
    fun sendNotification(notification: Notification)
}
