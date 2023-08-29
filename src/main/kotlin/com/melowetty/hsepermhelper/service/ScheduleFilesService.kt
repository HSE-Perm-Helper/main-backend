package com.melowetty.hsepermhelper.service

import java.io.InputStream

interface ScheduleFilesService {
    /**
     * Fetch all schedule files from site and return as input stream
     * Returns empty list if schedules are not found
     * Calls update schedule event when schedule is changed
     *
     * @return list of schedule files as input stream
     */
    fun fetchScheduleFilesAsInputStream(): List<InputStream>

    /**
     * Get schedule files from memory without fetching new files
     *
     * @return list of schedule files as input stream from memory
     */
    fun getScheduleFilesAsInputStream(): List<InputStream>
}