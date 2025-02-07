package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.excel.model.ExcelSchedule
import java.io.InputStream

interface HseTimetableExcelParser {
    fun parseScheduleFromExcelAsInputStream(inputStream: InputStream): ExcelSchedule?
}