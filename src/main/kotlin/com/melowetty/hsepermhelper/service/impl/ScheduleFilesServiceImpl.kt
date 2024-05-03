package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.models.File
import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import com.melowetty.hsepermhelper.service.FilesCheckingChangesService
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduleFilesServiceImpl(
    private val eventPublisher: ApplicationEventPublisher,
    private val filesCheckingChangesService: FilesCheckingChangesService,
    private val repository: ScheduleFilesRepository
): ScheduleFilesService {
    @Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 1000 * 60 * 5)
    private fun autoFetchingSchedules() {
        val before = getScheduleFiles()
        repository.fetchScheduleFiles()
        val after = getScheduleFiles()
        val changes = filesCheckingChangesService.getChanges(before = before, after = after)
        if(changes.addedOrChanged.isNotEmpty() || changes.deleted.isNotEmpty()) {
            eventPublisher.publishEvent(changes)
        }
    }

    override fun getScheduleFiles(): List<File> {
        return repository.getScheduleFilesAsByteArray().map { File(data = it) }
    }
}