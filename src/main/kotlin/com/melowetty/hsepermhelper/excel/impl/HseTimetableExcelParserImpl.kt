package com.melowetty.hsepermhelper.excel.impl

import com.melowetty.hsepermhelper.annotation.Slf4j
import com.melowetty.hsepermhelper.annotation.Slf4j.Companion.log
import com.melowetty.hsepermhelper.excel.HseTimetableExcelParser
import com.melowetty.hsepermhelper.excel.HseTimetableScheduleTypeChecker
import com.melowetty.hsepermhelper.excel.HseTimetableSheetExcelParser
import com.melowetty.hsepermhelper.excel.model.ParsedExcelInfo
import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.model.excel.ExcelLesson
import com.melowetty.hsepermhelper.model.excel.ExcelSchedule
import com.melowetty.hsepermhelper.notification.ServiceWarnNotification
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.util.RowUtils.Companion.getCellValue
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Component

@Component
@Slf4j
class HseTimetableExcelParserImpl(
    private val sheetParser: HseTimetableSheetExcelParser,
    private val scheduleTypeChecker: HseTimetableScheduleTypeChecker,
    private val notificationService: NotificationService
) : HseTimetableExcelParser {

    private fun getWorkbook(inputStream: InputStream): Workbook {
        return WorkbookFactory.create(inputStream)
    }

    override fun parseScheduleFromExcelAsInputStream(inputStream: InputStream): ExcelSchedule? {
        try {
            val workbook = getWorkbook(inputStream)
            val lessons = mutableListOf<ExcelLesson>()
            val scheduleInfo = getScheduleInfo(workbook) ?: return null

            for (i in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(i)
                if (!filterSheet(sheet)) continue
                unmergeRegions(sheet)
                val parsedLessons = sheetParser.parseSheet(sheet, scheduleInfo)
                lessons.addAll(parsedLessons)
            }

            lessons.sortBy { it.time }

            return ExcelSchedule(
                number = scheduleInfo.number,
                start = scheduleInfo.startDate,
                end = scheduleInfo.endDate,
                lessons = lessons,
                scheduleType = scheduleInfo.type
            )
        } catch (exception: Exception) {
            log.error(
                "Произошла ошибка во время обработки файла с расписанием! " +
                        "Stacktrace: ", exception
            )

            notificationService.sendNotification(ServiceWarnNotification(
                "Произошла ошибка во время обработки файла с расписанием! " +
                        "Stacktrace: ${exception.stackTraceToString()}"
            ))
            return null
        }
    }

    private fun getScheduleInfo(workbook: Workbook): ParsedScheduleInfo? {
        return if (workbook.numberOfSheets >= 2) getScheduleInfoBySheet(workbook.getSheetAt(1))
        else getScheduleInfoBySheet(workbook.getSheetAt(0))
    }

    private fun getScheduleInfoBySheet(sheet: Sheet): ParsedScheduleInfo? {
        unmergeRegions(sheet)
        return sheet.getRow(1).getCellValue(3)?.let { parseScheduleInfo(it) }
    }

    fun parseScheduleInfo(scheduleInfo: String): ParsedScheduleInfo? {
        val scheduleInfoRegex = Regex("([\\d\\.]+)")
        val scheduleInfoMatches = scheduleInfoRegex.findAll(scheduleInfo).toMutableList()

        val scheduleNumber = (scheduleInfoMatches.first().groups[1]?.value?.trim())?.toIntOrNull()

        if (scheduleInfoMatches.count() == 3) {
            scheduleInfoMatches.removeFirst()
        }

        val datePattern = DateTimeFormatter.ofPattern("ddMMyyyy")

        val scheduleStart = scheduleInfoMatches.first().groups[1]?.value?.let { LocalDate.parse(it.replace(".", ""), datePattern) } ?: return null

        scheduleInfoMatches.removeFirst()

        val scheduleEnd = scheduleInfoMatches.first().groups[1]?.value?.let { LocalDate.parse(it.replace(".", ""), datePattern) } ?: return null

        val scheduleType = scheduleTypeChecker.getScheduleType(
            ParsedExcelInfo(
                scheduleNumber, scheduleStart, scheduleEnd
            )
        )

        return ParsedScheduleInfo(
            number = scheduleNumber,
            startDate = scheduleStart,
            endDate = scheduleEnd,
            type = scheduleType,
        )
    }

    private fun unmergeRegions(sheet: Sheet) {
        for (region in sheet.mergedRegions) {
            val cellValue = sheet.getRow(region.firstRow).getCellValue(region.firstColumn)
            for (cell in region) {
                sheet.getRow(cell.row).getCell(cell.column).setCellValue(cellValue)
            }
        }
    }

    private fun filterSheet(sheet: Sheet): Boolean {
        return sheet.sheetName.lowercase() != "доц"
    }
}