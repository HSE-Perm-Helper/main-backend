package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.excel.model.ExcelLesson
import com.melowetty.hsepermhelper.excel.model.ParsedCellInfo

interface HseTimetableCellExcelParser {
    fun parseLesson(cellInfo: ParsedCellInfo): List<ExcelLesson>
}