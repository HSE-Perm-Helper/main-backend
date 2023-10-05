package com.melowetty.hsepermhelper.service

import Schedule
import com.melowetty.hsepermhelper.models.ScheduleInfo
import java.time.LocalDateTime

interface DataService {
    /**
     * Get last schedules, which were fetched
     *
     * @return list of saved schedules
     */
    fun getSavedSchedules(): List<ScheduleInfo>

    /**
     * Save schedules info
     *
     * @param schedules schedules, which must be saved
     */
    fun saveSchedules(schedules: List<Schedule>)

    /**
     * Get last start time of server
     *
     * @return start time of server
     */
    fun getStartTime(): LocalDateTime
}