package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.models.v2.ScheduleV2
import com.melowetty.hsepermhelper.models.v2.ScheduleV2.Companion.toScheduleInfo
import com.melowetty.hsepermhelper.dto.UserDto
import com.melowetty.hsepermhelper.events.ScheduleAddedEvent
import com.melowetty.hsepermhelper.events.ScheduleChangedForUserEvent
import com.melowetty.hsepermhelper.events.common.EventType
import com.melowetty.hsepermhelper.events.internal.ScheduleChangedEvent
import com.melowetty.hsepermhelper.events.internal.UsersChangedEvent
import com.melowetty.hsepermhelper.exceptions.ScheduleNotFoundException
import com.melowetty.hsepermhelper.models.*
import com.melowetty.hsepermhelper.models.v2.LessonV2
import com.melowetty.hsepermhelper.repository.ScheduleRepository
import com.melowetty.hsepermhelper.service.EventService
import com.melowetty.hsepermhelper.service.ScheduleService
import com.melowetty.hsepermhelper.service.UserFilesService
import com.melowetty.hsepermhelper.service.UserService
import com.melowetty.hsepermhelper.utils.FileUtils
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.util.*

@Service
class ScheduleServiceImpl(
    private val scheduleRepository: ScheduleRepository,
    private val userService: UserService,
    private val userFilesService: UserFilesService,
    private val eventService: EventService,
    private val env: Environment
): ScheduleService {
    private fun filterSchedules(schedules: List<ScheduleV2>, user: UserDto): List<ScheduleV2> {
        val filteredSchedules = mutableListOf<ScheduleV2>()
        schedules
            .filter {
                if(user.settings.includeQuarterSchedule.not()) {
                    it.scheduleType != ScheduleType.QUARTER_SCHEDULE
                }
                else
                    true
            }
            .forEach { schedule ->
            filteredSchedules.add(
                filterSchedule(schedule, user)
            )
        }
        return filteredSchedules
    }

    private fun filterSchedule(schedule: ScheduleV2, user: UserDto): ScheduleV2 {
        val filteredLessons = schedule.lessons.filter { lesson: LessonV2 ->
            if (lesson.subGroup != null) lesson.group == user.settings.group
                    && lesson.subGroup == user.settings.subGroup
            else lesson.group == user.settings.group
        }.filter {
            if (it.lessonType != LessonType.COMMON_ENGLISH) true
            else user.settings.includeCommonEnglish
        }.filter {
            if (it.lessonType != LessonType.COMMON_MINOR) true
            else user.settings.includeCommonMinor
        }
        return schedule.copy(
            lessons = filteredLessons
        )
    }

    override fun getUserSchedulesByTelegramId(telegramId: Long): List<ScheduleV2> {
        val user = userService.getByTelegramId(telegramId)
        return filterSchedules(scheduleRepository.getSchedules(), user)
    }

    override fun getUserSchedulesById(id: UUID): List<ScheduleV2> {
        val user = userService.getById(id)
        return filterSchedules(scheduleRepository.getSchedules(), user)
    }


    override fun getScheduleResource(id: UUID): Resource {
        val schedules = getUserSchedulesById(id)
        return FileUtils.convertSchedulesToCalendarFile(schedules)
    }

    @EventListener
    fun handleUsersChanging(event: UsersChangedEvent) {
        if(event.type == EventType.ADDED) {
            refreshScheduleFile(user = event.source)
        }
        else if(event.type == EventType.EDITED) {
            refreshScheduleFile(user = event.source)
        }
    }

    @EventListener
    fun handleScheduleChanging(event: ScheduleChangedEvent) {
        val editedSchedules = event.changes.getOrDefault(EventType.EDITED, null)
        val addedSchedules = event.changes.getOrDefault(EventType.ADDED, null)
        addedSchedules?.forEach { addedSchedule ->
            if(addedSchedule.after != null) {
                val schedule = addedSchedule.after
                val users = mutableListOf<Long>()
                if(schedule.scheduleType == ScheduleType.QUARTER_SCHEDULE) {
                    users.addAll(userService.getAllUsers()
                        .filter { it.settings.isEnabledNewQuarterScheduleNotifications }
                        .map { it.telegramId })
                } else {
                    users.addAll(userService.getAllUsers()
                        .filter { it.settings.isEnabledNewCommonScheduleNotifications }
                        .map { it.telegramId })
                }
                val scheduleAddedEvent = ScheduleAddedEvent(
                    targetSchedule = schedule.toScheduleInfo(),
                    users = users,
                )
                eventService.addEvent(scheduleAddedEvent)
            }
        }
        editedSchedules?.forEach {
            if (it.before != null && it.after != null) {
                val users = mutableSetOf<Long>()
                userService.getAllUsers()
                    .filter { user ->
                        (it.after.scheduleType == ScheduleType.QUARTER_SCHEDULE && user.settings.includeQuarterSchedule) ||
                                it.after.scheduleType != ScheduleType.QUARTER_SCHEDULE
                    }
                    .distinctBy { "${it.settings.group} ${it.settings.subGroup}" }.forEach { user ->
                        val before = filterSchedule(it.before, user)
                        val after = filterSchedule(it.after, user)
                        if (before.lessons.toHashSet() != after.lessons.toHashSet()) {
                            users.addAll(userService.getAllUsers().filter {
                                it.settings.group == user.settings.group
                                    && it.settings.subGroup == user.settings.subGroup }
                                .map { it.telegramId })
                        }
                    }
                if (users.isNotEmpty()) {
                    val scheduleChangedEvent = ScheduleChangedForUserEvent(
                        targetSchedule = it.after.toScheduleInfo(),
                        users = users.toList()
                    )
                    eventService.addEvent(scheduleChangedEvent)
                }
            }
        }
        refreshScheduleFiles()
    }

    override fun getScheduleFileByTelegramId(baseUrl: String, telegramId: Long): ScheduleFileLinks {
        if (getUserSchedulesByTelegramId(telegramId).isEmpty()) throw ScheduleNotFoundException("Расписание для пользователя не найдено!")
        val user = userService.getByTelegramId(telegramId)
        if(!user.settings.isEnabledRemoteCalendar) {
            userService.updateUserSettings(
                telegramId,
                settings = user.settings.copy(
                    isEnabledRemoteCalendar = true,
                )
            )
        }
        val link = "${baseUrl}${env.getProperty("server.servlet.context-path")}/files/user_files/${user.id}/${SCHEDULE_FILE}"
        return ScheduleFileLinks(
            linkForDownload = link,
            linkForRemoteCalendar = link
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
        try {
            userService.getAllUsers()
                .filter {
                    it.settings.isEnabledRemoteCalendar
                }.forEach {
                refreshScheduleFile(user = it)
            }
        } catch (e: Exception) {
            println("Произошла ошибка с обновлением расписаний пользователей!")
            e.printStackTrace()
        }
    }

    override fun refreshScheduleFile(user: UserDto) {
        if(!user.settings.isEnabledRemoteCalendar) return
        userFilesService.storeFile(user, getScheduleResource(user.id), SCHEDULE_FILE)
    }

    companion object {
        const val SCHEDULE_FILE = "schedule.ics"
    }

}