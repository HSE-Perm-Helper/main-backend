package com.melowetty.hsepermhelper.consumer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.melowetty.hsepermhelper.messaging.event.task.ChangeDetectionTask
import com.melowetty.hsepermhelper.messaging.event.task.NewTimetableNotifyTask
import com.melowetty.hsepermhelper.messaging.event.task.Task
import com.melowetty.hsepermhelper.service.impl.timetable.TimetableChangeDetectionService
import com.melowetty.hsepermhelper.service.impl.timetable.TimetableNotificationService
import com.melowetty.hsepermhelper.util.LoggingUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("app.message-broker.type", havingValue = "kafka")
class TasksConsumer(
    private val objectMapper: ObjectMapper,
    private val timetableChangeDetectionService: TimetableChangeDetectionService,
    private val timetableNotificationService: TimetableNotificationService,
) {

    @KafkaListener(
        topics = ["\${app.message-broker.kafka.topics.tasks}"],
        groupId = "\${app.message-broker.kafka.group-id}",
        containerFactory = "kafkaListenerContainerFactoryHashMap",
    )
    @RetryableTopic(
        attempts = "6",
        autoCreateTopics = "true",
        backoff = Backoff(1000, multiplier = 5.0, maxDelay = 3_125_000),
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        exclude = [JsonMappingException::class, JsonProcessingException::class],
    )
    fun consumeTask(taskAsMap: Map<String, Any?>) {
        LoggingUtils.executeWithRequestIdContext {
            try {
                val task = objectMapper.convertValue<Task>(taskAsMap)

                when (task) {
                    is ChangeDetectionTask -> {
                        timetableChangeDetectionService.detectAndProcessChanges(
                            task.timetableId,
                            task.oldData,
                            task.newData
                        )
                    }

                    is NewTimetableNotifyTask -> {
                        timetableNotificationService.notifyAboutAddedTimetables(task.timetables)
                    }
                }
            } catch (e: RuntimeException) {
                logger.error(e) { "Got exception when consumed task" }
                throw e
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}