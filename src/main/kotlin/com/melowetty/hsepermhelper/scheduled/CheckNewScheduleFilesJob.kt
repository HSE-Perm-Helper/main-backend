package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.service.FilesCheckingChangesService
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Deprecated("Remove when old timetables flow is not needed")
class CheckNewScheduleFilesJob(
    private val eventPublisher: ApplicationEventPublisher,
    private val filesCheckingChangesService: FilesCheckingChangesService,
    private val scheduleFilesService: ScheduleFilesService
) {
    @Scheduled(fixedRate = 1000 * 60 * 10, initialDelay = 1000 * 60 * 1)
    fun fetchScheduleFilesAndPublishEvents() {
        logger.info { "Fetching new schedule files" }
        val before = scheduleFilesService.getScheduleFiles()
        scheduleFilesService.fetchScheduleFiles()
        val after = scheduleFilesService.getScheduleFiles()
        val changes = filesCheckingChangesService.getChanges(before = before, after = after)
        if (changes.addedOrChanged.isNotEmpty() || changes.deleted.isNotEmpty()) {
            logger.info { "Schedule files has changed" }
            eventPublisher.publishEvent(changes)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}