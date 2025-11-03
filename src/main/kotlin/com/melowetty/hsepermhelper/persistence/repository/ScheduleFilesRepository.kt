package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.domain.model.file.File

interface ScheduleFilesRepository {
    /**
     * Get schedule files
     * Returns empty list if schedules are not found
     *
     * @return list of schedule files
     */
    fun getScheduleFiles(): List<File>

    fun fetchScheduleFiles(): List<File>
}