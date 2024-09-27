package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.Lesson
import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleInfo
import java.time.LocalDate
import java.util.UUID

interface ScheduleService {
    /**
     * Get all schedules for user by telegram id
     *
     * @param telegramId user telegram id
     * @return all user schedules
     */
    fun getUserSchedulesByTelegramId(telegramId: Long): List<Schedule>

    /**
     * Get all schedules info
     *
     * @return all schedules
     */
    fun getAvailableSchedules(): List<ScheduleInfo>

    /**
     * Get user schedule by telegram id and by start and end dates
     *
     * @param telegramId user telegram id
     * @param start start of schedule
     * @param end end of schedule
     *
     * @return user schedule filtered by start and end dates
     */
    fun getUserScheduleByTelegramId(telegramId: Long, start: LocalDate, end: LocalDate): Schedule

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

    fun getTodayLessons(telegramId: Long): List<Lesson>

    fun getTomorrowLessons(telegramId: Long): List<Lesson>
}