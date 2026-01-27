package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared

import com.melowetty.hsepermhelper.domain.model.timetable.impl.GroupBasedLesson
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedCellInfo

interface HseTimetableCellExcelParser {
    fun parseLesson(cellInfo: ParsedCellInfo): List<GroupBasedLesson>
}