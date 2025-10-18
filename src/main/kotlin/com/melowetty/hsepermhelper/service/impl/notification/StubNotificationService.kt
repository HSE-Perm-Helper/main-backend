package com.melowetty.hsepermhelper.service.impl.notification

import com.melowetty.hsepermhelper.notification.KafkaNotificationV2
import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.service.NotificationService
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*

class StubNotificationService : NotificationService {
    override fun sendNotification(notification: Notification) {
        logger.info { "Received notification $notification" }
    }

    override fun sendNotificationV2(notification: KafkaNotificationV2) {
        logger.info { "Received notification $notification" }
    }

    override fun sendUserNotification(userId: UUID, notification: KafkaNotificationV2) {
        logger.info { "User $userId received notification $notification" }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}