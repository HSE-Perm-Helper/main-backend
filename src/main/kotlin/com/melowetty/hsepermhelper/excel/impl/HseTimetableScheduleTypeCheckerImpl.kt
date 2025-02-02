package com.melowetty.hsepermhelper.excel.impl

import com.melowetty.hsepermhelper.excel.HseTimetableScheduleTypeChecker
import com.melowetty.hsepermhelper.excel.model.ParsedExcelInfo
import com.melowetty.hsepermhelper.model.schedule.ScheduleType
import org.springframework.stereotype.Component

@Component
class HseTimetableScheduleTypeCheckerImpl : HseTimetableScheduleTypeChecker {
    override fun getScheduleType(excelInfo: ParsedExcelInfo): ScheduleType {
        val (number, start, end) = excelInfo
        var scheduleType = ScheduleType.WEEK_SCHEDULE
        if (number == null) {
            scheduleType = ScheduleType.SESSION_SCHEDULE
        } else if (end.toEpochDay() - start.toEpochDay() > 7) {
            scheduleType = ScheduleType.QUARTER_SCHEDULE
        }
        return scheduleType
    }
}