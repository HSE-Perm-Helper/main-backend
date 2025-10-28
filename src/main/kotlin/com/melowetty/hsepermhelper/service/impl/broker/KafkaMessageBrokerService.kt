package com.melowetty.hsepermhelper.service.impl.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.melowetty.hsepermhelper.config.kafka.KafkaTopicsConfig
import com.melowetty.hsepermhelper.domain.model.event.KafkaNotification
import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.notification.NotificationV2
import com.melowetty.hsepermhelper.service.MessageBrokerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
@ConditionalOnProperty("app.message-broker.type", havingValue = "kafka")
class KafkaMessageBrokerService(
    private val kafkaTopicsConfig: KafkaTopicsConfig,
    private val kafkaTemplateUserEvents: KafkaTemplate<String, Any?>,
    private val kafkaTemplateNotificationV1: KafkaTemplate<String, KafkaNotification>,
    private val kafkaTemplateKafkaNotificationV2: KafkaTemplate<String, Map<String, Any?>>,
    private val objectMapper: ObjectMapper,
) : MessageBrokerService {

    override fun sendUserEvent(userId: UUID, eventType: UserEventType) {
        kafkaTemplateUserEvents.send(kafkaTopicsConfig.userEvents, eventType.toString(), mapOf(USER_EVENT_USER_ID_FIELD to userId))
    }

    override fun sendNotificationV1(notification: Notification) {
        kafkaTemplateNotificationV1.send(
            kafkaTopicsConfig.baseNotifications, KafkaNotification(
                notification.getNotificationType(),
                payload = notification,
            )
        )
    }

    override fun sendNotificationV2(userId: UUID?, notification: NotificationV2) {
        val notificationAsMap = convertNotificationToMap(notification)
        notificationAsMap[NOTIFICATION_USER_ID_FIELD] = userId

        kafkaTemplateKafkaNotificationV2.send(kafkaTopicsConfig.newNotifications, notificationAsMap)
    }

    override fun sendBatchNotificationsV2(userIds: List<UUID>, notification: NotificationV2) {
        val notificationAsMap = convertNotificationToMap(notification)
        notificationAsMap[NOTIFICATION_USER_ID_FIELD] = userIds

        kafkaTemplateKafkaNotificationV2.send(kafkaTopicsConfig.newNotifications, notificationAsMap)
    }

    private fun convertNotificationToMap(notification: NotificationV2): MutableMap<String, Any?> {
        val notificationAsStr = objectMapper.writeValueAsString(notification)
        val notificationAsMap = objectMapper.readValue<Map<String, Any?>>(notificationAsStr).toMutableMap()
        return notificationAsMap
    }

    companion object {
        private const val USER_EVENT_USER_ID_FIELD = "source"

        private const val NOTIFICATION_USER_ID_FIELD = "userId"
    }
}