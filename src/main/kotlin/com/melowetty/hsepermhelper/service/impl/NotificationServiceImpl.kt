package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.domain.model.event.KafkaNotification
import com.melowetty.hsepermhelper.domain.model.event.KafkaNotificationV2
import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.service.NotificationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val kafkaTemplateKafkaNotification: KafkaTemplate<String, KafkaNotification>,
    private val kafkaTemplateKafkaNotificationV2: KafkaTemplate<String, KafkaNotificationV2>
) : NotificationService {
    @Value("\${spring.kafka.topic.base-notifications}")
    private lateinit var baseNotificationsTopic: String

    @Value("\${spring.kafka.topic.new-notifications}")
    private lateinit var newNotificationsTopic: String

    override fun sendNotification(notification: Notification) {
        kafkaTemplateKafkaNotification.send(
            baseNotificationsTopic, KafkaNotification(
                notification.getNotificationType(),
                payload = notification,
            )
        )
    }

    override fun sendNotificationV2(notification: KafkaNotificationV2) {
        kafkaTemplateKafkaNotificationV2.send(newNotificationsTopic, notification)
    }
}