package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.basic

import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableProcessor
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.TimetableInfoUtils
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.TimetableLessonsUtils
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.TimetableParseUtils
import com.melowetty.hsepermhelper.domain.model.timetable.EducationType
import com.melowetty.hsepermhelper.domain.model.timetable.ExcelTimetable
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableSource
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.stereotype.Component

@Component
class BasicExcelTimetableProcessor: ExcelTimetableProcessor {
    override fun process(data: Workbook): List<ExcelTimetable> {
        val scheduleInfo = TimetableInfoUtils.getTimetableInfoIteratively(data)
            ?: throw IllegalArgumentException("Can't find timetable info")

        val lessons = TimetableParseUtils.parseSheets(
            data,
            sheetParser = { sheet ->
                TimetableLessonsUtils.parseSheet(sheet, scheduleInfo) { cellInfo ->
                    BasicTimetableCellParser.parseLesson(cellInfo)
                }
            }
        )

        return listOf(
            ExcelTimetable(
                number = scheduleInfo.number,
                lessons = lessons,
                start = scheduleInfo.startDate,
                end = scheduleInfo.endDate,
                type = scheduleInfo.type,
                educationType = EducationType.BACHELOR_OFFLINE,
                isParent = true,
                source = InternalTimetableSource.EXCEL,
            )
        )
    }

    override fun priority(): Int {
        return -1
    }

    override fun isParseable(name: String): Boolean {
        return true // Default parser
    }
}