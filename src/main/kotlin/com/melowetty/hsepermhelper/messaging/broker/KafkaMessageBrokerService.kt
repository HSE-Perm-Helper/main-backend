package com.melowetty.hsepermhelper.messaging.broker

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.melowetty.hsepermhelper.config.kafka.KafkaTopicsConfig
import com.melowetty.hsepermhelper.domain.model.event.KafkaNotification
import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.messaging.event.notification.Notification
import com.melowetty.hsepermhelper.messaging.event.notification.NotificationV2
import com.melowetty.hsepermhelper.messaging.event.task.ChangeDetectionTask
import com.melowetty.hsepermhelper.messaging.event.task.NewTimetableNotifyTask
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
@ConditionalOnProperty("app.message-broker.type", havingValue = "kafka")
class KafkaMessageBrokerService(
    private val kafkaTopicsConfig: KafkaTopicsConfig,
    private val kafkaTemplate: KafkaTemplate<String, Any?>,
    private val objectMapper: ObjectMapper,
) : MessageBrokerService {
    override fun submitTimetableChangeDetection(task: ChangeDetectionTask) {
        kafkaTemplate.send(kafkaTopicsConfig.tasks, task)
    }

    override fun submitNewTimetableNotifyTask(task: NewTimetableNotifyTask) {
        kafkaTemplate.send(kafkaTopicsConfig.tasks, task)
    }

    override fun sendUserEvent(userId: UUID, eventType: UserEventType) {
        kafkaTemplate.send(kafkaTopicsConfig.userEvents, eventType.toString(), mapOf(USER_EVENT_USER_ID_FIELD to userId))
    }

    override fun sendNotificationV1(notification: Notification) {
        kafkaTemplate.send(
            kafkaTopicsConfig.baseNotifications, KafkaNotification(
                notification.getNotificationType(),
                payload = notification,
            )
        )
    }

    override fun sendNotificationV2(userId: UUID?, notification: NotificationV2) {
        val notificationAsMap = convertNotificationToMap(notification)
        notificationAsMap[NOTIFICATION_USER_ID_FIELD] = userId

        kafkaTemplate.send(kafkaTopicsConfig.newNotifications, notificationAsMap)
    }

    override fun sendBatchNotificationsV2(userIds: List<UUID>, notification: NotificationV2) {
        val notificationAsMap = convertNotificationToMap(notification)
        notificationAsMap[NOTIFICATION_USER_ID_FIELD] = userIds

        kafkaTemplate.send(kafkaTopicsConfig.newNotifications, notificationAsMap)
    }

    private fun convertNotificationToMap(notification: NotificationV2): MutableMap<String, Any?> {
        return objectMapper.convertValue(notification, object : TypeReference<MutableMap<String, Any?>>() {})
    }

    companion object {
        private const val USER_EVENT_USER_ID_FIELD = "source"

        private const val NOTIFICATION_USER_ID_FIELD = "userId"
    }
}