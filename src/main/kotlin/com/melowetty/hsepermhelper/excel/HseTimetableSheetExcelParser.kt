package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.excel.model.ExcelLesson
import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import org.apache.poi.ss.usermodel.Sheet

interface HseTimetableSheetExcelParser {
    fun parseSheet(sheet: Sheet, scheduleInfo: ParsedScheduleInfo): List<ExcelLesson>
}