package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.timetable.model.InternalLesson
import com.melowetty.hsepermhelper.excel.model.ParsedCellInfo
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson

interface HseTimetableCellExcelParser {
    fun parseLesson(cellInfo: ParsedCellInfo): List<GroupBasedLesson>
}