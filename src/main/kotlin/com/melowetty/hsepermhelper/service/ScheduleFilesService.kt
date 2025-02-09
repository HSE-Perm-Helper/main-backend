package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.file.File

interface ScheduleFilesService {
    /**
     * Get schedule files from memory without fetching new files
     *
     * @return list of schedule files from memory
     */
    fun getScheduleFiles(): List<File>

    fun fetchScheduleFiles()
}