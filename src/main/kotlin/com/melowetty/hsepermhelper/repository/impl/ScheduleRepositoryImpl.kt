package com.melowetty.hsepermhelper.repository.impl

import com.melowetty.hsepermhelper.excel.HseTimetableExcelParser
import com.melowetty.hsepermhelper.exception.ScheduleNotFoundException
import com.melowetty.hsepermhelper.model.FilesChanging
import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.repository.ScheduleRepository
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import com.melowetty.hsepermhelper.service.SchedulesCheckingChangesService
import com.melowetty.hsepermhelper.util.ScheduleUtils
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ScheduleRepositoryImpl(
    private val eventPublisher: ApplicationEventPublisher,
    private val scheduleFilesService: ScheduleFilesService,
    private val schedulesCheckingChangesService: SchedulesCheckingChangesService,
    private val timetableExcelParser: HseTimetableExcelParser,
) : ScheduleRepository {
    private var schedules = listOf<Schedule>()

    @EventListener(ApplicationReadyEvent::class)
    fun firstScheduleFetching() {
        fetchSchedules()
    }

    override fun getSchedules(): List<Schedule> {
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

    override fun getAvailableCourses(): List<Int> {
        if (schedules.isEmpty()) throw ScheduleNotFoundException("Расписание не найдено!")
        val courses = schedules.flatMap { it.lessons }
            .asSequence()
            .map { it.course }
            .toSortedSet()
            .toList()
        if (courses.isEmpty()) throw RuntimeException("Возникли проблемы с обработкой расписания!")
        return courses
    }

    override fun getAvailablePrograms(course: Int): List<String> {
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

    override fun getAvailableGroups(course: Int, program: String): List<String> {
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

    override fun getAvailableSubgroups(course: Int, program: String, group: String): List<Int> {
        if (schedules.isEmpty()) throw ScheduleNotFoundException("Расписание не найдено!")
        val groups = getAvailableGroups(course, program)
        if (groups.isEmpty()) throw IllegalArgumentException("Группа не найдена в расписании!")
        val groupNumRegex = Regex("[А-Яа-яЁёa-zA-Z]+-\\d*-(\\d*)")
        try {
            val matches = groupNumRegex.find(groups.last())
            val lastGroupNumMatch = matches!!.groups[1]
            val lastGroupNum = lastGroupNumMatch!!.value.toInt()
            return (1..lastGroupNum * 2).toList()
        } catch (e: Exception) {
            throw RuntimeException("Возникли проблемы с обработкой группы!")
        }
    }
}