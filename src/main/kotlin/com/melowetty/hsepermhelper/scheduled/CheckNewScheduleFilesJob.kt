package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.annotation.Slf4j
import com.melowetty.hsepermhelper.annotation.Slf4j.Companion.log
import com.melowetty.hsepermhelper.service.FilesCheckingChangesService
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@Slf4j
class CheckNewScheduleFilesJob(
    private val eventPublisher: ApplicationEventPublisher,
    private val filesCheckingChangesService: FilesCheckingChangesService,
    private val scheduleFilesService: ScheduleFilesService
) {
    @Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 1000 * 60 * 5)
    fun fetchScheduleFilesAndPublishEvents() {
        log.debug("Fetching new schedule files")
        val before = scheduleFilesService.getScheduleFiles()
        scheduleFilesService.fetchScheduleFiles()
        val after = scheduleFilesService.getScheduleFiles()
        val changes = filesCheckingChangesService.getChanges(before = before, after = after)
        if(changes.addedOrChanged.isNotEmpty() || changes.deleted.isNotEmpty()) {
            log.debug("Schedule files has changed")
            eventPublisher.publishEvent(changes)
        }
    }
}