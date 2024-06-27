package com.melowetty.hsepermhelper.util

import org.apache.poi.ss.usermodel.Row

class RowUtils {
    companion object {
        fun Row.getCellValue(cellNum: Int): String {
            return getCell(cellNum).stringCellValue
        }
    }
}