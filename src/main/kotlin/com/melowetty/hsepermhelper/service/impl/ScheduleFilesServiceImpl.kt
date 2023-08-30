package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.models.ScheduleFile
import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class ScheduleFilesServiceImpl(
    private val repository: ScheduleFilesRepository
): ScheduleFilesService {
    private var scheduleFiles: List<ScheduleFile> = listOf()
    final override fun fetchScheduleFilesAsInputStream(): List<InputStream> {
        scheduleFiles = repository.fetchScheduleFilesAsInputStream().map { ScheduleFile(file = it) }
        return scheduleFiles.map { it.file }
    }

    override fun getScheduleFilesAsInputStream(): List<InputStream> {
        return scheduleFiles.map { it.file }
    }

    @Scheduled(fixedRate = 1000 * 60 * 5)
    private fun autoFetchingSchedules() {
        println("Cron task: fetching schedule")
        fetchScheduleFilesAsInputStream()
        println("Schedules are fetched")
    }
}