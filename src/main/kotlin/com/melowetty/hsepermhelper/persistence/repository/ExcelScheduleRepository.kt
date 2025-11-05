package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.domain.model.file.FilesChanging
import com.melowetty.hsepermhelper.excel.HseTimetableExcelParser
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import com.melowetty.hsepermhelper.service.SchedulesCheckingChangesService
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
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
    private var schedules = listOf<ExcelTimetable>()

    @EventListener(ApplicationReadyEvent::class)
    fun firstScheduleFetching() {
        fetchSchedules()
    }

    fun getSchedules(): List<InternalTimetable> {
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
            timetableExcelParser.parseScheduleFromExcel(it)
        }
        schedules = ScheduleUtils.normalizeSchedules(newSchedules) as List<ExcelTimetable>
    }

    fun getAvailableCourses(): List<Int> {
        if (schedules.isEmpty()) throw ScheduleNotFoundException(SCHEDULE_NOT_FOUND_EXCEPTION_MESSAGE)
        val courses = schedules.flatMap { it.lessons }
            .asSequence()
            .map { it.course() }
            .toSortedSet()
            .toList()

        require(courses.isNotEmpty()) {
            "В расписании не найдены курсы!"
        }
        return courses
    }

    fun getAvailablePrograms(course: Int): List<String> {
        if (schedules.isEmpty()) throw ScheduleNotFoundException(SCHEDULE_NOT_FOUND_EXCEPTION_MESSAGE)
        val programs = schedules.flatMap { it.lessons }
            .asSequence()
            .filter { it.course() == course }
            .map { it.program() }
            .toSortedSet()
            .toList()

        require(programs.isNotEmpty()) {
            "Курс не найден в расписании!"
        }
        return programs
    }

    fun getAvailableGroups(course: Int, program: String): List<String> {
        if (schedules.isEmpty()) throw ScheduleNotFoundException(SCHEDULE_NOT_FOUND_EXCEPTION_MESSAGE)
        val groups = schedules.flatMap { it.lessons }
            .asSequence()
            .filter { it.course() == course && it.program() == program }
            .map { it.group }
            .toSortedSet()
            .toList()

        require(groups.isNotEmpty()) {
            "Программа не найдена в расписании!"
        }
        return groups
    }

    companion object {
        private const val SCHEDULE_NOT_FOUND_EXCEPTION_MESSAGE = "Расписание не найдено!"
    }
}