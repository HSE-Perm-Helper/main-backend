package com.melowetty.hsepermhelper.job

import com.melowetty.hsepermhelper.context.JobRunContextHolder
import com.melowetty.hsepermhelper.domain.model.context.JobRunContext
import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import com.melowetty.hsepermhelper.service.impl.timetable.ExcelTimetableFilesProcessService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.PersistJobDataAfterExecution
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
class TimetableFilesObserveJob(
    val scheduleFilesRepository: ScheduleFilesRepository,
    val excelTimetableFilesProcessService: ExcelTimetableFilesProcessService,
) : Job {

    override fun execute(context: JobExecutionContext) {
        val jobDataMap = context.jobDetail.jobDataMap

        val jobRunId = generateJobId()
        val prevJobRunId = jobDataMap[PREV_RUN_ID_KEY] as String?

        val runContext = JobRunContext(jobRunId, prevJobRunId)
        JobRunContextHolder.set(runContext)

        try {
            MDC.put(JOB_ID_LOGGING_KEY, jobRunId)
            val current = scheduleFilesRepository.fetchScheduleFiles()
            logger.info { "Fetched new timetables files" }
            excelTimetableFilesProcessService.processOrNothing(current)
        } finally {
            MDC.remove(JOB_ID_LOGGING_KEY)
            JobRunContextHolder.clear()
        }

        jobDataMap[PREV_RUN_ID_KEY] = jobRunId
    }

    companion object {
        private const val JOB_ID_LOGGING_KEY = "job_run_id"
        private const val PREV_RUN_ID_KEY = "prevRunId"

        private val logger = KotlinLogging.logger {  }

        private fun generateJobId(): String {
            return Instant.now().toEpochMilli().toString()
        }
    }
}