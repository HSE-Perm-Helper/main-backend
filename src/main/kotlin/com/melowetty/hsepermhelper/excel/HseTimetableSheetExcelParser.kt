package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.model.excel.ExcelLesson
import org.apache.poi.ss.usermodel.Sheet

interface HseTimetableSheetExcelParser {
    fun parseSheet(sheet: Sheet, scheduleInfo: ParsedScheduleInfo): List<ExcelLesson>
}