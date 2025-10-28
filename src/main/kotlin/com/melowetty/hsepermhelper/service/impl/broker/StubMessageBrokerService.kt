package com.melowetty.hsepermhelper.service.impl.broker

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.notification.NotificationV2
import com.melowetty.hsepermhelper.service.MessageBrokerService
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID

class StubMessageBrokerService : MessageBrokerService {
    override fun sendUserEvent(userId: UUID, eventType: UserEventType) {
        logger.info { "Stub user event added: $userId, $eventType" }
    }

    override fun sendNotificationV1(notification: Notification) {
        logger.info { "Stub notification v1 added: $notification" }
    }

    override fun sendNotificationV2(userId: UUID?, notification: NotificationV2) {
        logger.info { "Stub notification v2 added: $userId, $notification" }
    }

    override fun sendBatchNotificationsV2(userIds: List<UUID>, notification: NotificationV2) {
        logger.info { "Stub batch notification v2 added: $userIds, $notification" }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}