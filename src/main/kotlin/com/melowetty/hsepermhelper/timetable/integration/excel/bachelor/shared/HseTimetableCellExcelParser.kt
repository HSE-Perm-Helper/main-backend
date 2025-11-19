package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared

import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedCellInfo
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson

interface HseTimetableCellExcelParser {
    fun parseLesson(cellInfo: ParsedCellInfo): List<GroupBasedLesson>
}