package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import org.apache.poi.ss.usermodel.Workbook

interface ExcelScheduleProcessor {
    fun process(data: Workbook): List<ExcelTimetable>
    fun priority(): Int = 0
    fun isParseable(name: String): Boolean
}