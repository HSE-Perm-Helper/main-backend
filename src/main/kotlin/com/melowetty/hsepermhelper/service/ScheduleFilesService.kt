package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.models.ScheduleFile

interface ScheduleFilesService {
    /**
     * Fetch all schedule files from site and return their
     * Returns empty list if schedules are not found
     * Calls update schedule event when schedule is changed
     *
     * @return list of schedule files
     */
    fun fetchScheduleFiles(): List<ScheduleFile>

    /**
     * Get schedule files from memory without fetching new files
     *
     * @return list of schedule files from memory
     */
    fun getScheduleFiles(): List<ScheduleFile>
}