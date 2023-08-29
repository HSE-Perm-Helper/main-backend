package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class ScheduleFilesServiceImpl(
    private val repository: ScheduleFilesRepository
): ScheduleFilesService {
    private var scheduleFiles = fetchScheduleFilesAsInputStream()
    final override fun fetchScheduleFilesAsInputStream(): List<InputStream> {
        scheduleFiles = repository.fetchScheduleFilesAsInputStream()
        return scheduleFiles
    }

    override fun getScheduleFilesAsInputStream(): List<InputStream> {
        return scheduleFiles
    }
}