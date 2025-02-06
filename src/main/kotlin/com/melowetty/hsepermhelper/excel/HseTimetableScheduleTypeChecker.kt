package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.excel.model.ParsedExcelInfo
import com.melowetty.hsepermhelper.model.schedule.ScheduleType

interface HseTimetableScheduleTypeChecker {
    fun getScheduleType(excelInfo: ParsedExcelInfo): ScheduleType
}