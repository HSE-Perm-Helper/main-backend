package com.melowetty.hsepermhelper.notification

data class ServiceWarnNotification(
    val message: String
) : Notification() {
    override fun getNotificationType(): String {
        return "SERVICE_WARNING"
    }
}