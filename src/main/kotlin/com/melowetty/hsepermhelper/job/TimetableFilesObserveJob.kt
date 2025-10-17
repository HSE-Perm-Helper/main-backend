package com.melowetty.hsepermhelper.job

import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import com.melowetty.hsepermhelper.service.impl.ExcelTimetableFilesProcessService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TimetableFilesObserveJob(
    val scheduleFilesRepository: ScheduleFilesRepository,
    val excelTimetableFilesProcessService: ExcelTimetableFilesProcessService,
) {
    @Scheduled(fixedRate = 1000 * 60 * 10, initialDelay = 1000 * 60 * 0)
    fun observe() {
        val current = scheduleFilesRepository.fetchScheduleFiles()
        logger.info { "Fetched new timetables files" }
        excelTimetableFilesProcessService.processOrNothing(current)
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}