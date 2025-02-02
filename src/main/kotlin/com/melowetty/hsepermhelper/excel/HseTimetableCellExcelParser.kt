package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.excel.model.ParsedCellInfo
import com.melowetty.hsepermhelper.model.lesson.Lesson

interface HseTimetableCellExcelParser {
    fun parseLesson(cellInfo: ParsedCellInfo): List<Lesson>
}