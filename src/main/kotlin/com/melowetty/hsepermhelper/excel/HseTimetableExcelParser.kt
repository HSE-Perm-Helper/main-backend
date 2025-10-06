package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable

interface HseTimetableExcelParser {
    fun parseScheduleFromExcel(file: File): ExcelTimetable?
}