package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.online

import com.melowetty.hsepermhelper.context.ExcelTimetableParseContextHolder
import com.melowetty.hsepermhelper.domain.model.context.ParseError
import com.melowetty.hsepermhelper.domain.model.lesson.CycleTime
import com.melowetty.hsepermhelper.domain.model.lesson.LessonTime
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableType
import com.melowetty.hsepermhelper.domain.model.timetable.impl.GroupBasedLesson
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.CellInfo
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedCellInfo
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.util.RowUtils.Companion.getCellValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Utility for parsing online bachelor schedule lessons from Excel.
 * Online schedules have different time format: "2  11.10-12.30" instead of multiline "2\n11:10-12:30"
 * and date+day in a single cell: "Пятница 09.01.2026"
 */
object OnlineTimetableLessonsUtils {
    private val logger = KotlinLogging.logger {  }

    fun parseSheet(
        sheet: Sheet,
        scheduleInfo: ParsedScheduleInfo,
        cellParser: (ParsedCellInfo) -> List<GroupBasedLesson>,
    ): List<GroupBasedLesson> {
        val lessons = mutableListOf<GroupBasedLesson>()
        val groups = parseGroups(sheet)
        val previousData = PreviousData()

        for (rowNum in 3 until sheet.lastRowNum) {
            val row = sheet.getRow(rowNum)
            val (parsedLessons, action) = parseRow(
                RowData(
                    row = row,
                    scheduleInfo = scheduleInfo,
                    groups = groups,
                    previousData = previousData
                ),
                cellParser
            )

            if (action == Action.BREAK) break
            if (action == Action.CONTINUE) continue

            lessons.addAll(parsedLessons)
        }
        return lessons
    }

    private fun parseGroups(sheet: Sheet): Map<Int, String> {
        val groups = mutableMapOf<Int, String>()
        for (cellNum in 2 until sheet.getRow(2).physicalNumberOfCells) {
            val group = sheet.getRow(2).getCellValue(cellNum) ?: continue
            if (group != "" && groups.containsValue(group).not()) {
                groups[cellNum] = group
            }
        }
        return groups
    }

    private fun parseRow(rowData: RowData, parser: (ParsedCellInfo) -> List<GroupBasedLesson>): Pair<List<GroupBasedLesson>, Action> {
        val row = rowData.row
        val lessons = mutableListOf<GroupBasedLesson>()
        val (lessonTime, action) = getLessonTime(rowData)

        if (action != Action.NOTHING) return Pair(listOf(), action)

        for (cellNum in 2 until row.physicalNumberOfCells) {
            val cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
            val cellValue = cell.stringCellValue
            if (cellValue.isEmpty()) continue

            val lessonInfo = getParsedLessonInfo(cellNum, rowData, lessonTime!!) ?: break

            try {
                val parsedLessons = parser(lessonInfo)
                lessons.addAll(parsedLessons)
            } catch (e: RuntimeException) {
                val error = ParseError(
                    sheet = rowData.row.sheet.sheetName,
                    cell = cell.address.formatAsString(),
                    cellValue = cellValue,
                    exception = e,
                )
                ExcelTimetableParseContextHolder.addError(error)
                logger.warn(e) { "Error when parse lessons, error: $error" }
            }
        }

        return Pair(lessons, Action.NOTHING)
    }

    private fun getParsedLessonInfo(cellNum: Int, rowData: RowData, lessonTime: LessonTime): ParsedCellInfo? {
        val group = rowData.groups[cellNum] ?: return null
        val isUnderlined = checkIsUnderlined(rowData.row.getCell(cellNum))

        return ParsedCellInfo(
            cellInfo = CellInfo(
                value = rowData.row.getCellValue(cellNum) ?: return null,
                group = group,
                time = lessonTime,
                isUnderlined = isUnderlined
            ),
            scheduleInfo = rowData.scheduleInfo
        )
    }

    private fun getLessonTime(rowData: RowData): Pair<LessonTime?, Action> {
        val dateCell = rowData.row.getCellValue(0)
        val timeCell = rowData.row.getCellValue(1) ?: return Pair(null, Action.CONTINUE)

        val (startTime, endTime) = parseTime(timeCell) ?: return Pair(null, Action.CONTINUE)

        val lessonTime: LessonTime

        if (dateCell.isNullOrEmpty()) {
            if (rowData.scheduleInfo.type != InternalTimetableType.BACHELOR_QUARTER_TIMETABLE) {
                if (rowData.previousData.prevDay.isNotEmpty()) {
                    val day = parseDayOfWeek(rowData.previousData.prevDay) ?: return Pair(null, Action.CONTINUE)
                    lessonTime = CycleTime(day, startTime, endTime)
                } else {
                    return Pair(null, Action.BREAK)
                }
            } else {
                val day = parseDayOfWeek(rowData.previousData.prevDay) ?: return Pair(null, Action.CONTINUE)
                lessonTime = CycleTime(day, startTime, endTime)
            }
        } else {
            val date = parseDate(dateCell)
            if (date != null) {
                lessonTime = ScheduledTime(date, startTime, endTime)
            } else {
                val day = parseDayOfWeek(dateCell) ?: return Pair(null, Action.CONTINUE)
                lessonTime = CycleTime(day, startTime, endTime)
            }
            rowData.previousData.prevDay = dateCell
        }

        return Pair(lessonTime, Action.NOTHING)
    }

    /**
     * Parses time from online format: "2  11.10-12.30" (dot-separated, single cell).
     */
    private fun parseTime(timeCell: String): Pair<String, String>? {
        val normalized = timeCell.replace(".", ":")
        val match = Regex("([0-9]{1,2}:[0-9]{2})\\s*-\\s*([0-9]{1,2}:[0-9]{2})").find(normalized)
            ?: return null
        return Pair(match.groupValues[1], match.groupValues[2])
    }

    /**
     * Extracts date from "Пятница 09.01.2026" format.
     */
    private fun parseDate(dateCell: String): LocalDate? {
        val match = Regex("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}").find(dateCell) ?: return null
        return try {
            LocalDate.parse(match.value, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) {
            logger.warn { "Failed to parse date: ${match.value}" }
            null
        }
    }

    /**
     * Extracts day of week from "Пятница" or "Пятница 09.01.2026".
     */
    private fun parseDayOfWeek(str: String): DayOfWeek? {
        val day = str.split(Regex("\\s+|\\|")).firstOrNull()?.lowercase() ?: return null
        return when (day) {
            "понедельник" -> DayOfWeek.MONDAY
            "вторник" -> DayOfWeek.TUESDAY
            "среда" -> DayOfWeek.WEDNESDAY
            "четверг" -> DayOfWeek.THURSDAY
            "пятница" -> DayOfWeek.FRIDAY
            "суббота" -> DayOfWeek.SATURDAY
            "воскресенье" -> DayOfWeek.SUNDAY
            else -> null
        }
    }

    private fun checkIsUnderlined(cell: Cell): Boolean {
        val font = cell.row.sheet.workbook.getFontAt(cell.cellStyle.fontIndex)
        return font.underline != Font.U_NONE
    }

    internal enum class Action { CONTINUE, BREAK, NOTHING }

    internal data class RowData(
        val row: Row,
        val scheduleInfo: ParsedScheduleInfo,
        val groups: Map<Int, String>,
        val previousData: PreviousData
    )

    internal class PreviousData {
        var prevDay = ""
    }
}
