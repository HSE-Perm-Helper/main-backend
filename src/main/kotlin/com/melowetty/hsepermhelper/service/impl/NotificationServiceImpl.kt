package com.melowetty.hsepermhelper.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.melowetty.hsepermhelper.annotation.Slf4j
import com.melowetty.hsepermhelper.domain.model.user.UserRole
import com.melowetty.hsepermhelper.domain.model.event.KafkaNotification
import com.melowetty.hsepermhelper.notification.KafkaNotificationV2
import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.notification.NotificationRecipient
import com.melowetty.hsepermhelper.repository.UserRepository
import com.melowetty.hsepermhelper.service.NotificationService
import java.util.UUID
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
@Slf4j
class NotificationServiceImpl(
    private val kafkaTemplateKafkaNotification: KafkaTemplate<String, KafkaNotification>,
    private val kafkaTemplateKafkaNotificationV2: KafkaTemplate<String, Map<String, Any?>>,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
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
        val recipientType = notification.recipient

        val userRole = when(recipientType) {
            NotificationRecipient.NONE -> {
                sendNotificationToKafka(userId = null, notification)
                return
            }
            NotificationRecipient.ALL -> UserRole.USER
            NotificationRecipient.ADMIN -> UserRole.ADMIN
            NotificationRecipient.SERVICE_ADMIN -> UserRole.SERVICE_ADMIN
        }

        // TODO сделать получение пользователей через пагинацию
        val users = userRepository.findAllByRolesContains(userRole)

        for (user in users) {
            sendNotificationToKafka(user.id, notification)
        }
    }

    override fun sendUserNotification(userId: UUID, notification: KafkaNotificationV2) {
        sendNotificationToKafka(userId, notification)
    }

    private fun sendNotificationToKafka(userId: UUID?, notification: KafkaNotificationV2) {
        val notificationAsStr = objectMapper.writeValueAsString(notification)
        val notificationAsMap = objectMapper.readValue<Map<String, Any?>>(notificationAsStr).toMutableMap()
        notificationAsMap[USER_ID_FIELD] = userId

        kafkaTemplateKafkaNotificationV2.send(newNotificationsTopic, notificationAsMap)
    }

    companion object {
        private const val USER_ID_FIELD = "userId"
    }
}