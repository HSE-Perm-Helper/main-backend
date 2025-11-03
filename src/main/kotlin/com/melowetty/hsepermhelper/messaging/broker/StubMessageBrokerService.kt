package com.melowetty.hsepermhelper.messaging.broker

import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.messaging.event.notification.Notification
import com.melowetty.hsepermhelper.messaging.event.notification.NotificationV2
import com.melowetty.hsepermhelper.messaging.event.task.ChangeDetectionTask
import com.melowetty.hsepermhelper.messaging.event.task.NewTimetableNotifyTask
import com.melowetty.hsepermhelper.service.impl.timetable.TimetableChangeDetectionService
import com.melowetty.hsepermhelper.service.impl.timetable.TimetableNotificationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import java.util.*

class StubMessageBrokerService(

) : MessageBrokerService {
    @Lazy
    @Autowired
    private lateinit var timetableChangeDetectionService: TimetableChangeDetectionService

    @Lazy
    @Autowired
    private lateinit var timetableNotificationService: TimetableNotificationService

    override fun submitTimetableChangeDetection(task: ChangeDetectionTask) {
        logger.info { "Stub change detection task added: $task" }
        logger.info { "Running change detection task: $task" }
        timetableChangeDetectionService.detectAndProcessChanges(task.timetableId, task.oldData, task.newData)
    }

    override fun submitNewTimetableNotifyTask(task: NewTimetableNotifyTask) {
        logger.info { "Stub new timetable notify task added: $task" }
        logger.info { "Running new timetable notify task: $task" }
        timetableNotificationService.notifyAboutAddedTimetables(task.timetables)
    }

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