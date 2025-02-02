package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.model.schedule.Schedule
import java.io.InputStream

interface HseTimetableExcelParser {
    fun parseScheduleFromExcelAsInputStream(inputStream: InputStream): Schedule?
}