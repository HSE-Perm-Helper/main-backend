package com.melowetty.hsepermhelper.excel.impl

import com.melowetty.hsepermhelper.annotation.Slf4j
import com.melowetty.hsepermhelper.annotation.Slf4j.Companion.log
import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.excel.HseTimetableExcelParser
import com.melowetty.hsepermhelper.excel.model.ParsedExcelInfo
import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.notification.ServiceWarnNotification
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.BachelorTimetableSheetExcelParser
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.TimetableTypeUtils
import com.melowetty.hsepermhelper.timetable.model.EducationType
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableSource
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import com.melowetty.hsepermhelper.util.RowUtils.Companion.getCellValue
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Component
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
@Slf4j
class HseTimetableExcelParserImpl(
    private val sheetParser: BachelorTimetableSheetExcelParser,
    private val notificationService: NotificationService
) : HseTimetableExcelParser {
    companion object {
        private const val NUMS_DETECT_REGEX_PATTERN = "[\\d\\.]+"
        private const val SCHEDULE_DATES_REGEX_PATTERN = "[\\(]{1}(.+)[\\)]{1}"

        private val numsDetectRegex = NUMS_DETECT_REGEX_PATTERN.toRegex()
        private val scheduleDatesRegex = SCHEDULE_DATES_REGEX_PATTERN.toRegex()
    }

    private fun getWorkbook(inputStream: InputStream): Workbook {
        return WorkbookFactory.create(inputStream)
    }

    override fun parseScheduleFromExcel(file: File): ExcelTimetable? {
        try {
            val workbook = getWorkbook(file.toInputStream())
            preprocessWorkbook(workbook)
            val lessons = mutableListOf<GroupBasedLesson>()
            val scheduleInfo = getScheduleInfo(workbook)
                ?: throw RuntimeException("Не получилось обработать информацию о расписании")

            for (i in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(i)
                if (!filterSheet(sheet)) continue
                val parsedLessons = sheetParser.parseSheet(sheet, scheduleInfo)
                lessons.addAll(parsedLessons)
            }

            lessons.sortBy { it.time }

            return ExcelTimetable(
                number = scheduleInfo.number,
                start = scheduleInfo.startDate,
                end = scheduleInfo.endDate,
                lessons = lessons,
                type = scheduleInfo.type,
                educationType = EducationType.BACHELOR_OFFLINE,
                isParent = true,
                source = InternalTimetableSource.EXCEL,
            )
        } catch (exception: Exception) {
            log.error(
                "Произошла ошибка во время обработки файла с расписанием! Файл: ${file.name}\n" +
                        "Stacktrace: ", exception
            )

            notificationService.sendNotificationV2(ServiceWarnNotification(
                "Произошла ошибка во время обработки файла с расписанием! Файл: ${file.name}\n" +
                        "Stacktrace: ${exception.stackTraceToString()}"
            ))
            return null
        }
    }

    private fun preprocessWorkbook(workbook: Workbook) {
        for (sheet in workbook.sheetIterator()) {
            unmergeRegions(sheet)
        }
    }

    private fun unmergeRegions(sheet: Sheet) {
        for (region in sheet.mergedRegions) {
            val cellValue = sheet.getRow(region.firstRow).getCellValue(region.firstColumn)
            for (cell in region) {
                sheet.getRow(cell.row).getCell(cell.column).setCellValue(cellValue)
            }
        }
    }

    private fun getScheduleInfo(workbook: Workbook): ParsedScheduleInfo? {
        for (sheet in workbook.sheetIterator()) {
            val info = getScheduleInfoBySheet(sheet)
            if (info != null) return info
        }

        return null
    }

    private fun getScheduleInfoBySheet(sheet: Sheet): ParsedScheduleInfo? {
        val row = sheet.getRow(1)
        for (i in 0 until row.physicalNumberOfCells) {
            try {
                val value = row.getCellValue(i)?.let { parseScheduleInfo(it) }
                if (value != null) return value
            } catch (e: RuntimeException) {
                log.trace(e.stackTraceToString())
                continue
            }
        }

        return null
    }

    fun parseScheduleInfo(scheduleInfo: String): ParsedScheduleInfo? {
        val scheduleNumber = parseScheduleNumber(scheduleInfo)
        val (scheduleStart, scheduleEnd) = parseScheduleDates(scheduleInfo)
            ?: throw IllegalArgumentException("Не удалось распознать даты расписания, номер: $scheduleNumber, текст: $scheduleInfo")

        val scheduleType = TimetableTypeUtils.getScheduleType(
            ParsedExcelInfo(scheduleNumber, scheduleStart, scheduleEnd)
        )

        return ParsedScheduleInfo(
            number = scheduleNumber,
            startDate = scheduleStart,
            endDate = scheduleEnd,
            type = scheduleType,
        )
    }

    private fun parseScheduleNumber(scheduleInfo: String): Int? {
        val scheduleNumberInfo = scheduleInfo.replace(scheduleDatesRegex, "").trim()
        return numsDetectRegex.find(scheduleNumberInfo)?.value?.toIntOrNull()
    }

    private fun parseScheduleDates(scheduleInfo: String): Pair<LocalDate, LocalDate>? {
        val scheduleDatesInfo = scheduleDatesRegex.find(scheduleInfo)?.groups?.get(1)?.value
            ?: return null

        val matches = numsDetectRegex.findAll(scheduleDatesInfo).toMutableList()
        val scheduleStart = matches.firstOrNull()?.let { parseScheduleDate(it.value) } ?: return null

        matches.removeFirst()

        if (matches.isEmpty()) return Pair(scheduleStart, scheduleStart)

        val scheduleEnd = matches.firstOrNull()?.let { parseScheduleDate(it.value) } ?: return null

        return Pair(scheduleStart, scheduleEnd)
    }

    private fun parseScheduleDate(value: String): LocalDate {
        val datePattern = DateTimeFormatter.ofPattern("ddMMyyyy")
        return LocalDate.parse(value.replace(".", ""), datePattern)
    }

    private fun filterSheet(sheet: Sheet): Boolean {
        return sheet.sheetName.lowercase() != "доц"
    }
}