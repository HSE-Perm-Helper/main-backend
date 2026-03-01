package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared

import com.melowetty.hsepermhelper.domain.model.timetable.impl.GroupBasedLesson
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedScheduleInfo
import org.apache.poi.ss.usermodel.Sheet

interface BachelorTimetableSheetExcelParser {
    fun parseSheet(sheet: Sheet, scheduleInfo: ParsedScheduleInfo): List<GroupBasedLesson>
}