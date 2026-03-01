package com.melowetty.hsepermhelper.config

import com.melowetty.hsepermhelper.job.TimetableFilesObserveJob
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@EnableScheduling
class QuartzConfiguration {
    @Value("\${jobs.timetable-files-obverse.cron}")
    private lateinit var timetableFilesObserveCron: String

    @Bean
    fun timetableFilesObserveJobDetail(): JobDetail {
        return JobBuilder
            .newJob(TimetableFilesObserveJob::class.java).withIdentity("TimetableFilesObserveJob")
            .requestRecovery(true)
            .storeDurably()
            .build()
    }

    @Bean
    fun timetableFilesObserveJobTrigger(): Trigger {
        return TriggerBuilder.newTrigger().forJob(timetableFilesObserveJobDetail())
            .withIdentity("TimetableFilesObserveJobTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule(timetableFilesObserveCron))
            .build()

    }

    @Bean
    fun scheduler(
        triggers: List<Trigger>,
        factory: SchedulerFactoryBean,
        transactionManager: PlatformTransactionManager
    ): Scheduler {
        factory.setWaitForJobsToCompleteOnShutdown(true)
        val scheduler = factory.scheduler
        factory.setOverwriteExistingJobs(true)
        factory.setTransactionManager(transactionManager)
        rescheduleTriggers(triggers, scheduler)
        scheduler.start()
        return scheduler
    }

    private fun rescheduleTriggers(
        triggers: List<Trigger>,
        scheduler: Scheduler
    ) {
        triggers.forEach {
            if (!scheduler.checkExists(it.key)) {
                scheduler.scheduleJob(it)
            } else {
                scheduler.rescheduleJob(it.key, it)
            }
        }
    }
}