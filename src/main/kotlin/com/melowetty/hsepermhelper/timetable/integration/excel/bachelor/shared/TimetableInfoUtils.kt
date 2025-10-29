package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared

import com.melowetty.hsepermhelper.excel.model.ParsedExcelInfo
import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.util.RowUtils.Companion.getCellValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TimetableInfoUtils {
    private const val NUMS_DETECT_REGEX_PATTERN = "[\\d\\.]+"
    private const val SCHEDULE_DATES_REGEX_PATTERN = "[\\(]{1}(.+)[\\)]{1}"

    private val numsDetectRegex = NUMS_DETECT_REGEX_PATTERN.toRegex()
    private val scheduleDatesRegex = SCHEDULE_DATES_REGEX_PATTERN.toRegex()

    private val logger = KotlinLogging.logger {  }

    fun getTimetableInfoIteratively(workbook: Workbook): ParsedScheduleInfo? {
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
                logger.trace(e) { "Не удалось распознать расписание, sheet ${sheet.sheetName}" }
                continue
            }
        }

        return null
    }

    private fun parseScheduleInfo(scheduleInfo: String): ParsedScheduleInfo {
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
}