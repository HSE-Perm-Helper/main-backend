package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.excel.model.ParsedCellInfo
import com.melowetty.hsepermhelper.model.excel.ExcelLesson

interface HseTimetableCellExcelParser {
    fun parseLesson(cellInfo: ParsedCellInfo): List<ExcelLesson>
}