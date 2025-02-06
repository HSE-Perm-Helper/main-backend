package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.model.excel.ExcelSchedule
import java.io.InputStream

interface HseTimetableExcelParser {
    fun parseScheduleFromExcelAsInputStream(inputStream: InputStream): ExcelSchedule?
}