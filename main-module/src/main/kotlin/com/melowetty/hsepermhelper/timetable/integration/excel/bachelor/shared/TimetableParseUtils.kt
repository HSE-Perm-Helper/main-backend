package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared

import com.melowetty.hsepermhelper.domain.model.timetable.impl.GroupBasedLesson
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook

object TimetableParseUtils {
    fun parseSheets(
        workbook: Workbook,
        sheetParser: (Sheet) -> List<GroupBasedLesson>,
        filter: (Sheet) -> Boolean = { true }
    ): List<GroupBasedLesson> {
        val lessons = mutableListOf<GroupBasedLesson>()

        for (i in 0 until workbook.numberOfSheets) {
            val sheet = workbook.getSheetAt(i)
            if (!filter(sheet)) continue

            val parsedLessons = sheetParser(sheet)
            lessons.addAll(parsedLessons)
        }

        return lessons
    }
}