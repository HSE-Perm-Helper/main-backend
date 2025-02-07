package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.excel.model.ParsedExcelInfo

interface HseTimetableScheduleTypeChecker {
    fun getScheduleType(excelInfo: ParsedExcelInfo): ScheduleType
}