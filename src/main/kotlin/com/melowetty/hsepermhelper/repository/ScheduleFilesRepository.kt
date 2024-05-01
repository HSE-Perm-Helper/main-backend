package com.melowetty.hsepermhelper.repository

import java.io.InputStream

interface ScheduleFilesRepository {
    /**
     * Get schedule files
     * Returns empty list if schedules are not found
     *
     * @return list of schedule files as input stream
     */
    fun getScheduleFiles(): List<InputStream>

    fun fetchScheduleFilesAsInputStream()
}