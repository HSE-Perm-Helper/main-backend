package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.models.Schedule
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

    fun getAvailableCourses(): List<Int>

    fun getAvailablePrograms(course: Int): List<String>

    fun getAvailableGroups(course: Int, program: String): List<String>

    fun getAvailableSubgroups(course: Int, program: String, group: String): List<Int>
}