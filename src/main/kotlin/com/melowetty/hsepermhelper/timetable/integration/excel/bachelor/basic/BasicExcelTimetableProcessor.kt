package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.basic

import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableProcessor
import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableStorage
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.stereotype.Component

@Component
class BasicExcelTimetableProcessor(
    private val storage: ExcelTimetableStorage,
) : ExcelTimetableProcessor {
    override fun process(data: Workbook): List<ExcelTimetable> {
        TODO("Not yet implemented")
    }

    override fun priority(): Int {
        return -1
    }

    override fun isParseable(name: String): Boolean {
        return true // Default parser
    }
}