package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.events.internal.ScheduleFilesChangedEvent
import com.melowetty.hsepermhelper.models.ScheduleFile
import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduleFilesServiceImpl(
    private val eventPublisher: ApplicationEventPublisher,
    private val repository: ScheduleFilesRepository
): ScheduleFilesService {
    private var scheduleFiles: List<ScheduleFile> = listOf()
    final override fun fetchScheduleFiles(callEvents: Boolean): List<ScheduleFile> {
        val newScheduleFiles = repository.fetchScheduleFilesAsInputStream().map { ScheduleFile(file = it) }
        var event: ScheduleFilesChangedEvent? = null
        if (newScheduleFiles.size != scheduleFiles.size) {
            event = ScheduleFilesChangedEvent(
                newFiles = newScheduleFiles
            )
        } else {
            if(newScheduleFiles.any { scheduleFile ->
                    scheduleFiles.map { it.hashCode }.contains(scheduleFile.hashCode).not()
            }
                ) {
                event = ScheduleFilesChangedEvent(
                    newFiles = newScheduleFiles
                )
            }
        }
        scheduleFiles = newScheduleFiles
        if (event != null && callEvents) {
            eventPublisher.publishEvent(event)
        }
        return scheduleFiles
    }

    override fun getScheduleFiles(): List<ScheduleFile> {
        return scheduleFiles
    }

    @Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 1000 * 60 * 5)
    private fun autoFetchingSchedules() {
        println("Cron task: fetching schedule")
        fetchScheduleFiles()
        println("[Fetching schedule] Schedules fetching is done")
    }

    @EventListener
    fun handleScheduleFilesChanging(event: ScheduleFilesChangedEvent) {
        println("[Fetching schedule] Schedules are changed")
    }
}