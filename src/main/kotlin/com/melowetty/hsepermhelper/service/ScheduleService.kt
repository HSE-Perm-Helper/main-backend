package com.melowetty.hsepermhelper.service

import Schedule
import com.melowetty.hsepermhelper.dto.UserDto
import com.melowetty.hsepermhelper.models.ScheduleFileLinks
import org.springframework.core.io.Resource
import java.util.*

interface ScheduleService {
    /**
     * Get all schedules for user by telegram id
     *
     * @param telegramId user telegram id
     * @return all user schedules
     */
    fun getUserSchedulesByTelegramId(telegramId: Long): List<Schedule>

    /**
     * Get all schedules for user by id
     *
     * @param id user id
     * @return ll user schedules
     */
    fun getUserSchedulesById(id: UUID): List<Schedule>

    /**
     * Returns resource file for mobile calendar of schedule for user
     * @param id user's id
     * @return resource .ics file
     */
    fun getScheduleResource(id: UUID): Resource

    /**
     * Returns links for mobile calendar of schedule for user
     * @param baseUrl base url of server
     * @param id user's telegram id
     * @return schedule file object
     */
    fun getScheduleFileByTelegramId(baseUrl: String, telegramId: Long): ScheduleFileLinks

    fun getAvailableCourses(): List<Int>

    fun getAvailablePrograms(course: Int): List<String>

    fun getAvailableGroups(course: Int, program: String): List<String>

    fun getAvailableSubgroups(course: Int, program: String, group: String): List<Int>

    /**
     * Refresh schedule files for users
     *
     */
    fun refreshScheduleFiles()

    /**
     * Refresh schedule for user
     *
     * @param user user
     */
    fun refreshScheduleFile(user: UserDto)
}