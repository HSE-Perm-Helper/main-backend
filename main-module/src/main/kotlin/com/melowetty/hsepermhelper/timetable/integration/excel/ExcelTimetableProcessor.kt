package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import org.apache.poi.ss.usermodel.Workbook

interface ExcelTimetableProcessor {
    fun process(data: Workbook): List<ExcelTimetable>
    fun priority(): Int = 0
    fun isParseable(name: String): Boolean
}