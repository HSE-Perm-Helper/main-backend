package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared

import com.melowetty.hsepermhelper.excel.model.ParsedExcelInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType

object TimetableTypeUtils {
    fun getScheduleType(excelInfo: ParsedExcelInfo): InternalTimetableType {
        val (number, start, end) = excelInfo
        var scheduleType = InternalTimetableType.BACHELOR_WEEK_SCHEDULE

        if (number == null) {
            scheduleType = InternalTimetableType.BACHELOR_SESSION_SCHEDULE
        } else if (end.toEpochDay() - start.toEpochDay() > 7) {
            scheduleType = InternalTimetableType.BACHELOR_QUARTER_SCHEDULE
        }
        return scheduleType
    }
}