package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.user.UserRole
import com.melowetty.hsepermhelper.notification.NotificationV2
import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.notification.NotificationRecipient
import com.melowetty.hsepermhelper.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationService(
    private val userRepository: UserRepository,
    private val messageBrokerService: MessageBrokerService,
) {
    fun sendNotification(notification: Notification) {
        messageBrokerService.sendNotificationV1(notification)
    }

    fun sendNotificationV2(notification: NotificationV2) {
        val recipientType = notification.recipient

        val userRole = when(recipientType) {
            NotificationRecipient.NONE -> {
                sendNotificationToBroker(userId = null, notification)
                return
            }
            NotificationRecipient.ALL -> UserRole.USER
            NotificationRecipient.ADMIN -> UserRole.ADMIN
            NotificationRecipient.SERVICE_ADMIN -> UserRole.SERVICE_ADMIN
        }

        // TODO сделать получение пользователей через пагинацию
        val users = userRepository.findAllByRolesContains(userRole)

        for (user in users) {
            sendNotificationToBroker(user.id, notification)
        }
    }

    fun sendUserNotification(userId: UUID, notification: NotificationV2) {
        sendNotificationToBroker(userId, notification)
    }

    fun sendBatchUserNotification(userIds: List<UUID>, notification: NotificationV2) {
        messageBrokerService.sendBatchNotificationsV2(userIds, notification)
    }

    private fun sendNotificationToBroker(userId: UUID?, notification: NotificationV2) {
        messageBrokerService.sendNotificationV2(userId, notification)
    }
}
