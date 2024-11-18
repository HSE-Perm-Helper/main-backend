package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.model.Schedule

interface ScheduleRepository {
    /**
     * Get parsed schedules
     *
     * @return list of schedules
     */
    fun getSchedules(): List<Schedule>

    /**
     * Gets available courses
     *
     * @throws ScheduleNotFoundException throws when schedule not found
     * @throws RuntimeException throws when schedule do not have a courses
     * @return list of available courses
     */
    fun getAvailableCourses(): List<Int>

    /**
     * Gets available programs for this course
     *
     * @param course user's course
     * @throws ScheduleNotFoundException throws when schedule not found
     * @throws IllegalArgumentException throws when user's course not found in schedule
     * @return list of available programs
     */
    fun getAvailablePrograms(course: Int): List<String>

    /**
     * Gets available groups for this program and course
     *
     * @param course user's course
     * @param program user's program
     * @throws ScheduleNotFoundException throws when schedule not found
     * @throws IllegalArgumentException throws when user's program not found in schedule
     * @return list of available groups
     */
    fun getAvailableGroups(course: Int, program: String): List<String>
}