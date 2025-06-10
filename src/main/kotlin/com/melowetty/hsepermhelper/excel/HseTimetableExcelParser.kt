package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.excel.model.ExcelSchedule

interface HseTimetableExcelParser {
    fun parseScheduleFromExcel(file: File): ExcelSchedule?
}