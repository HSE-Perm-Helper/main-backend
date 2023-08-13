package com.melowetty.hsepermhelper.service.impl

import Schedule
import com.melowetty.hsepermhelper.dto.UserDto
import com.melowetty.hsepermhelper.exceptions.ScheduleNotFoundException
import com.melowetty.hsepermhelper.models.Lesson
import com.melowetty.hsepermhelper.models.ScheduleFile
import com.melowetty.hsepermhelper.repository.ScheduleRepository
import com.melowetty.hsepermhelper.service.FileStorageService
import com.melowetty.hsepermhelper.service.ScheduleService
import com.melowetty.hsepermhelper.service.UserService
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.util.*
import kotlin.io.path.Path

@Service
class ScheduleServiceImpl(
    private val scheduleRepository: ScheduleRepository,
    private val userService: UserService,
    private val fileStorageService: FileStorageService,
    private val env: Environment
): ScheduleService {
    init {
        refreshScheduleFiles()
    }
    private fun filterSchedule(schedule: Schedule, user: UserDto): Schedule {
        val filteredLessons = schedule.lessons.flatMap { it.value }.filter{ lesson: Lesson ->
            if (lesson.subGroup != null) lesson.group == user.settings?.group
                    && lesson.subGroup == user.settings.subGroup
            else lesson.group == user.settings?.group
        }
        val groupedLessons = filteredLessons.groupBy { it.date }
        return schedule.copy(
            lessons = groupedLessons
        )
    }

    private fun getCurrentSchedule(user: UserDto): Schedule {
        val schedule = scheduleRepository.getCurrentSchedule() ?: throw ScheduleNotFoundException("Расписание на текущую неделю не было найдено!")
        return filterSchedule(schedule, user)
    }

    override fun getCurrentSchedule(telegramId: Long): Schedule {
        val user = userService.getByTelegramId(telegramId)
        return getCurrentSchedule(user)
    }

    override fun getCurrentSchedule(id: UUID): Schedule {
        val user = userService.getById(id)
        return getCurrentSchedule(user)
    }

    private fun getNextSchedule(user: UserDto): Schedule {
        val schedule = scheduleRepository.getNextSchedule() ?: throw ScheduleNotFoundException("Расписание на текущую неделю не было найдено!")
        return filterSchedule(schedule, user)
    }

    override fun getNextSchedule(telegramId: Long): Schedule {
        val user = userService.getByTelegramId(telegramId)
        return getNextSchedule(user)
    }

    override fun getNextSchedule(id: UUID): Schedule {
        val user = userService.getById(id)
        return getNextSchedule(user)
    }

    override fun getScheduleResource(id: UUID): Resource {
        val currentSchedule = getCurrentSchedule(id)
        val nextSchedule = getNextSchedule(id)
        val lessons = currentSchedule.lessons.toMutableMap()
        nextSchedule.lessons.forEach { lessons[it.key] = it.value }
        val schedule = currentSchedule.copy(
            lessons = lessons
        )
        return schedule.toResource()
    }

    override fun getScheduleFileByTelegramId(id: Long): ScheduleFile {
        val schedule = getCurrentSchedule(id)
        val link = "${env.getProperty("app.baselink")}${env.getProperty("server.servlet.context-path")}/files/${id}/schedule.ics"
        return ScheduleFile(
            linkForDownload = link,
            linkForRemoteCalendar = "webcal://${link}"
        )
    }

    override fun getAvailableCourses(): List<Int> {
        return scheduleRepository.getAvailableCourses()
    }

    override fun getAvailablePrograms(course: Int): List<String> {
        return scheduleRepository.getAvailablePrograms(course = course)
    }

    override fun getAvailableGroups(course: Int, program: String): List<String> {
        return scheduleRepository.getAvailableGroups(course = course, program = program)
    }

    override fun getAvailableSubgroups(course: Int, program: String, group: String): List<Int> {
        return scheduleRepository.getAvailableSubgroups(course = course, program = program, group = group)
    }

    final override fun refreshScheduleFiles() {
        userService.getAllUsers().forEach {
            val path = Path(it.id.toString())
            fileStorageService.storeFile(path, getScheduleResource(it.id), "schedule.ics")
        }
    }
}