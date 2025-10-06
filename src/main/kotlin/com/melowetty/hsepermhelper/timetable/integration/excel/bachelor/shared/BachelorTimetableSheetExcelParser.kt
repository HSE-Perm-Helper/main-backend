package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared

import com.melowetty.hsepermhelper.timetable.model.InternalLesson
import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import org.apache.poi.ss.usermodel.Sheet

interface BachelorTimetableSheetExcelParser {
    fun parseSheet(sheet: Sheet, scheduleInfo: ParsedScheduleInfo): List<GroupBasedLesson>
}