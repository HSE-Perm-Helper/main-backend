package com.melowetty.hsepermhelper.consumer

import com.melowetty.hsepermhelper.messaging.event.task.TaskType
import com.melowetty.hsepermhelper.service.impl.timetable.TimetableChangeDetectionService
import com.melowetty.hsepermhelper.service.impl.timetable.TimetableNotificationService
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [JacksonAutoConfiguration::class, TasksConsumer::class])
class TasksConsumerTest {
    @Autowired
    private lateinit var tasksConsumer: TasksConsumer

    @MockkBean
    private lateinit var changeDetectionService: TimetableChangeDetectionService

    @MockkBean
    private lateinit var notificationService: TimetableNotificationService

    @Test
    fun `positive change detection task consume`() {
        every { changeDetectionService.detectAndProcessChanges(any(), any(), any()) } just Runs

        tasksConsumer.consumeTask(
            mapOf(
                "timetableId" to "1",
                "oldData" to emptyList<GroupBasedLesson>(),
                "newData" to emptyList<GroupBasedLesson>(),
                "taskType" to TaskType.CHANGE_DETECTION.name
            )
        )

        verify { changeDetectionService.detectAndProcessChanges("1", emptyList(), emptyList()) }
    }

    @Test
    fun `positive new timetable notify task consume`() {
        every {  notificationService.notifyAboutAddedTimetables(any()) } just Runs

        tasksConsumer.consumeTask(
            mapOf(
                "timetables" to emptyList<InternalTimetableInfo>(),
                "taskType" to TaskType.NEW_TIMETABLE_NOTIFY.name
            )
        )

        verify { notificationService.notifyAboutAddedTimetables(emptyList()) }
    }

    @Test
    fun `negative wrong task type task consume`() {
        Assertions.assertThrows(RuntimeException::class.java) {
            tasksConsumer.consumeTask(
                mapOf(
                    "taskType" to "undefined"
                )
            )
        }
    }
}