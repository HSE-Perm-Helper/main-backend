package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.domain.model.file.FilesChanging
import com.melowetty.hsepermhelper.excel.HseTimetableExcelParser
import com.melowetty.hsepermhelper.excel.model.ExcelSchedule
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import com.melowetty.hsepermhelper.service.SchedulesCheckingChangesService
import com.melowetty.hsepermhelper.util.ScheduleUtils
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ExcelScheduleRepository(
    private val eventPublisher: ApplicationEventPublisher,
    private val scheduleFilesService: ScheduleFilesService,
    private val schedulesCheckingChangesService: SchedulesCheckingChangesService,
    private val timetableExcelParser: HseTimetableExcelParser,
) {
    private var schedules = listOf<ExcelSchedule>()

    @EventListener(ApplicationReadyEvent::class)
    fun firstScheduleFetching() {
        fetchSchedules()
    }

    fun getSchedules(): List<ExcelSchedule> {
        return schedules
    }

    @EventListener
    fun handleScheduleFilesUpdate(event: FilesChanging) {
        val prevSchedules = schedules
        fetchSchedules()
        val newSchedules = schedules
        val changes = schedulesCheckingChangesService.getChanges(prevSchedules, newSchedules)
        if (changes.changed.isNotEmpty() || changes.deleted.isNotEmpty() || changes.added.isNotEmpty()) {
            eventPublisher.publishEvent(changes)
        }
    }

    private fun fetchSchedules() {
        val newSchedules = scheduleFilesService.getScheduleFiles().mapNotNull {
            timetableExcelParser.parseScheduleFromExcelAsInputStream(it.toInputStream())
        }
        schedules = ScheduleUtils.normalizeSchedules(newSchedules)
    }

    fun getAvailableCourses(): List<Int> {
        if (schedules.isEmpty()) throw ScheduleNotFoundException("Расписание не найдено!")
        val courses = schedules.flatMap { it.lessons }
            .asSequence()
            .map { it.course }
            .toSortedSet()
            .toList()
        if (courses.isEmpty()) throw RuntimeException("Возникли проблемы с обработкой расписания!")
        return courses
    }

    fun getAvailablePrograms(course: Int): List<String> {
        if (schedules.isEmpty()) throw ScheduleNotFoundException("Расписание не найдено!")
        val programs = schedules.flatMap { it.lessons }
            .asSequence()
            .filter { it.course == course }
            .map { it.programme }
            .toSortedSet()
            .toList()
        if (programs.isEmpty()) throw IllegalArgumentException("Курс не найден в расписании!")
        return programs
    }

    fun getAvailableGroups(course: Int, program: String): List<String> {
        if (schedules.isEmpty()) throw ScheduleNotFoundException("Расписание не найдено!")
        val groups = schedules.flatMap { it.lessons }
            .asSequence()
            .filter { it.course == course && it.programme == program }
            .map { it.group }
            .toSortedSet()
            .toList()
        if (groups.isEmpty()) throw IllegalArgumentException("Программа не найдена в расписании!")
        return groups
    }
}