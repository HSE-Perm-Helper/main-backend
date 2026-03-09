package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.online

import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableProcessor
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.TimetableInfoUtils
import com.melowetty.hsepermhelper.domain.model.timetable.EducationType
import com.melowetty.hsepermhelper.domain.model.timetable.ExcelTimetable
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableSource
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.stereotype.Component

@Component
class OnlineBachelorTimetableProcessor : ExcelTimetableProcessor {
    override fun process(data: Workbook): List<ExcelTimetable> {
        val timetables = mutableListOf<ExcelTimetable>()

        for (i in 0 until data.numberOfSheets) {
            val sheet = data.getSheetAt(i)
            val scheduleInfo = TimetableInfoUtils.getScheduleInfoBySheet(sheet) ?: continue

            val lessons = OnlineTimetableLessonsUtils.parseSheet(sheet, scheduleInfo) { cellInfo ->
                OnlineTimetableCellParser.parseLesson(cellInfo)
            }

            timetables.add(
                ExcelTimetable(
                    number = scheduleInfo.number,
                    lessons = lessons,
                    start = scheduleInfo.startDate,
                    end = scheduleInfo.endDate,
                    type = scheduleInfo.type,
                    educationType = EducationType.BACHELOR_ONLINE,
                    isParent = true,
                    source = InternalTimetableSource.EXCEL,
                )
            )
        }

        return timetables
    }

    override fun isParseable(name: String): Boolean {
        return name.contains("ОП")
    }
}
