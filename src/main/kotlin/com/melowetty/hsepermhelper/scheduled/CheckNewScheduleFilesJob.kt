package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.service.FilesCheckingChangesService
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CheckNewScheduleFilesJob(
    private val eventPublisher: ApplicationEventPublisher,
    private val filesCheckingChangesService: FilesCheckingChangesService,
    private val scheduleFilesService: ScheduleFilesService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 1000 * 60 * 5)
    fun fetchScheduleFilesAndPublishEvents() {
        logger.info("Fetching new schedule files")
        val before = scheduleFilesService.getScheduleFiles()
        scheduleFilesService.fetchScheduleFiles()
        val after = scheduleFilesService.getScheduleFiles()
        val changes = filesCheckingChangesService.getChanges(before = before, after = after)
        if(changes.addedOrChanged.isNotEmpty() || changes.deleted.isNotEmpty()) {
            logger.info("Schedule files has changed")
            eventPublisher.publishEvent(changes)
        }
    }
}