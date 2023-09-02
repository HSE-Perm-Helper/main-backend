package com.melowetty.hsepermhelper.repository

import java.io.InputStream

interface ScheduleFilesRepository {
    /**
     * Fetch all schedule files from site and return as input stream
     * Returns empty list if schedules are not found
     *
     * @return list of schedule files as input stream
     */
    fun fetchScheduleFilesAsInputStream(): List<InputStream>
}