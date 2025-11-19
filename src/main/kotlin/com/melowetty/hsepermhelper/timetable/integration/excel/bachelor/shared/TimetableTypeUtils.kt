package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared

import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedExcelInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType

object TimetableTypeUtils {
    fun getScheduleType(excelInfo: ParsedExcelInfo): InternalTimetableType {
        val (number, start, end) = excelInfo
        var scheduleType = InternalTimetableType.BACHELOR_WEEK_TIMETABLE

        if (number == null) {
            scheduleType = InternalTimetableType.BACHELOR_SESSION_TIMETABLE
        } else if (end.toEpochDay() - start.toEpochDay() > 7) {
            scheduleType = InternalTimetableType.BACHELOR_QUARTER_TIMETABLE
        }
        return scheduleType
    }
}