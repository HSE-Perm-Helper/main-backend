package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.model.KafkaNotification
import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.service.NotificationService
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val kafkaTemplate: KafkaTemplate<String, KafkaNotification>
) : NotificationService {
    override fun sendNotification(notification: Notification) {
        kafkaTemplate.send(
            "new-notification", KafkaNotification(
                notification.getNotificationType(),
                payload = notification,
            )
        )
    }
}