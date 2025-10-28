package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.messaging.broker.MessageBrokerService
import com.melowetty.hsepermhelper.messaging.event.task.ChangeDetectionTask
import com.melowetty.hsepermhelper.messaging.event.task.NewTimetableNotifyTask
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class ExcelTimetableChangeDispatcher(
    private val messageBrokerService: MessageBrokerService,
) {
    fun dispatchChangeDetection(
        newTimetable: ExcelTimetable,
        oldTimetable: ExcelTimetable
    ) {
        logger.info { "Dispatching change detection task for timetable ${newTimetable.id()}" }
        val task = buildChangeDetectionTask(newTimetable, oldTimetable)
        messageBrokerService.submitTimetableChangeDetection(task)
    }

    fun dispatchNewTimetableNotification(timetables: List<InternalTimetableInfo>) {
        logger.info { "Dispatching new timetable notification task" }
        val task = NewTimetableNotifyTask(timetables)
        messageBrokerService.submitNewTimetableNotifyTask(task)
    }

    private fun buildChangeDetectionTask(
        newTimetable: ExcelTimetable,
        oldTimetable: ExcelTimetable
    ): ChangeDetectionTask {
        return ChangeDetectionTask(
            timetableId = newTimetable.id(),
            oldData = extractComparisonData(oldTimetable),
            newData = extractComparisonData(newTimetable)
        )
    }

    private fun extractComparisonData(timetable: ExcelTimetable): List<GroupBasedLesson> {
        return timetable.lessons
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}