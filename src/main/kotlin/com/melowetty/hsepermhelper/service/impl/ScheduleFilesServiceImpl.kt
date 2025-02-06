package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.model.file.File
import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import org.springframework.stereotype.Service

@Service
class ScheduleFilesServiceImpl(
    private val repository: ScheduleFilesRepository
) : ScheduleFilesService {
    override fun getScheduleFiles(): List<File> {
        return repository.getScheduleFilesAsByteArray().map { File(data = it) }
    }

    override fun fetchScheduleFiles() {
        repository.fetchScheduleFiles()
    }
}