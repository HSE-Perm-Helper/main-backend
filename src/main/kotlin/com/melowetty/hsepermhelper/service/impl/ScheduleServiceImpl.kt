package com.melowetty.hsepermhelper.service.impl

import Schedule
import com.melowetty.hsepermhelper.dto.UserDto
import com.melowetty.hsepermhelper.events.EventType
import com.melowetty.hsepermhelper.events.ScheduleChangedEvent
import com.melowetty.hsepermhelper.events.UsersChangedEvent
import com.melowetty.hsepermhelper.exceptions.ScheduleNotFoundException
import com.melowetty.hsepermhelper.models.Lesson
import com.melowetty.hsepermhelper.models.LessonType
import com.melowetty.hsepermhelper.models.ScheduleFileLinks
import com.melowetty.hsepermhelper.models.ScheduleType
import com.melowetty.hsepermhelper.repository.ScheduleRepository
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
    private val env: Environment
): ScheduleService {
    init {
        refreshScheduleFiles()
    }
    private fun filterSchedules(schedules: List<Schedule>, user: UserDto): List<Schedule> {
        val filteredSchedules = mutableListOf<Schedule>()
        schedules
            .filter {
                if(user.settings?.includeQuarterSchedule?.not() == true) {
                    it.scheduleType != ScheduleType.QUARTER_SCHEDULE
                }
                else
                    true
            }
            .forEach { schedule ->
            val filteredLessons = schedule.lessons.flatMap { it.value }.filter { lesson: Lesson ->
                if (lesson.subGroup != null) lesson.group == user.settings?.group
                        && lesson.subGroup == user.settings.subGroup
                else lesson.group == user.settings?.group
            }.filter {
                if (it.lessonType != LessonType.COMMON_ENGLISH) true
                else user.settings?.includeCommonEnglish == true
            }.filter {
                if (it.lessonType != LessonType.COMMON_MINOR) true
                else user.settings?.includeCommonMinor == true
            }
            val groupedLessons = filteredLessons.groupBy { it.date }
            filteredSchedules.add(
                schedule.copy(
                    lessons = groupedLessons
                )
            )
        }
        return filteredSchedules
    }

    override fun getUserSchedulesByTelegramId(telegramId: Long): List<Schedule> {
        val user = userService.getByTelegramId(telegramId)
        return filterSchedules(scheduleRepository.getSchedules(), user)
    }

    override fun getUserSchedulesById(id: UUID): List<Schedule> {
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
        if(editedSchedules != null) {
            val changedForUserIds = mutableListOf<Long>()
            editedSchedules.forEach {
                val groupedAfter = it.after!!.lessons.values.flatten().groupBy { it.group }
                it.before!!.lessons.values.flatten().groupBy { it.group }.forEach { groupEntry ->
                    val groupMatch = groupedAfter.getOrDefault(groupEntry.key, null)
                    if(groupMatch != null) {
                        val subGroupGrouping = groupMatch.groupBy { it.subGroup }
                        groupEntry.value.groupBy { it.subGroup }.forEach subGroupForeach@ { subGroupEntry ->
                            val subGroupMatch = subGroupGrouping.getOrDefault(subGroupEntry.key, null)
                            if(subGroupMatch != null) {
                                if(subGroupMatch == subGroupEntry.value) {
                                    return@subGroupForeach
                                } else {
                                    changedForUserIds.addAll(userService.getAllUsers(groupEntry.key, subGroupEntry.key ?: 0).map { it.telegramId })
                                }
                            } else {
                                changedForUserIds.addAll(userService.getAllUsers(groupEntry.key, subGroupEntry.key ?: 0).map { it.telegramId })
                            }
                        }
                    } else {
                        changedForUserIds.addAll(userService.getAllUsers(groupEntry.key, 0).map { it.telegramId })
                    }
                }
            }
            changedForUserIds.distinct()
        }
        refreshScheduleFiles()
    }

    override fun getScheduleFileByTelegramId(baseUrl: String, telegramId: Long): ScheduleFileLinks {
        if (getUserSchedulesByTelegramId(telegramId).isEmpty()) throw ScheduleNotFoundException("Расписание для пользователя не найдено!")
        val user = userService.getByTelegramId(telegramId)
        val link = "${baseUrl}${env.getProperty("server.servlet.context-path")}/files/user_files/${user.id}/${SCHEDULE_FILE}"
        return ScheduleFileLinks(
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
        try {
            userService.getAllUsers()
                .filter {
                    it.settings?.isEnabledRemoteCalendar == true
                }.forEach {
                refreshScheduleFile(user = it)
            }
        } catch (e: Exception) {
            println("Произошла ошибка с обновлением расписаний пользователей!")
            e.printStackTrace()
        }
    }

    override fun refreshScheduleFile(user: UserDto) {
        userFilesService.storeFile(user, getScheduleResource(user.id), SCHEDULE_FILE)
    }

    companion object {
        const val SCHEDULE_FILE = "schedule.ics"
    }

}