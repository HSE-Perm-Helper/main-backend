package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared

import com.melowetty.hsepermhelper.context.ExcelTimetableParseContextHolder
import com.melowetty.hsepermhelper.domain.model.context.ParseError
import com.melowetty.hsepermhelper.domain.model.lesson.CycleTime
import com.melowetty.hsepermhelper.domain.model.lesson.LessonTime
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.CellInfo
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedCellInfo
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import com.melowetty.hsepermhelper.util.RowUtils.Companion.getCellValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TimetableLessonsUtils {
    private val logger = KotlinLogging.logger {  }

    fun extractProgram(group: String) = group.split("-").first()

    fun extractCourse(group: String): Int {
        val year = group.split("-")[1].toInt()
        val curDate = LocalDate.now()
        val curYear = curDate.year % 100

        if (curDate.monthValue < 9) {
            return curYear - year
        } else {
            return curYear - year + 1
        }
    }

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
        var unparsedDate = rowData.row.getCellValue(0)?.split("\n") ?: return Pair(null, Action.CONTINUE)
        val lessonTime: LessonTime

        val timeCell =
            rowData.row.getCellValue(1)?.split("\n")?.filter { it.isNotEmpty() } ?: return Pair(null, Action.CONTINUE)
        if (timeCell.size < 2) return Pair(null, Action.CONTINUE)

        val (startTime, endTime) = getStartAndEndTime(timeCell)

        if (unparsedDate.size < 2) {
            if (rowData.scheduleInfo.type != InternalTimetableType.BACHELOR_QUARTER_TIMETABLE) {
                if (rowData.previousData.prevDay.isNotEmpty()) {
                    unparsedDate = rowData.previousData.prevDay.split("\n")
                } else {
                    return Pair(null, Action.BREAK)
                }
            }
            val day = getDayOfWeek(unparsedDate[0]) ?: return Pair(null, Action.CONTINUE)
            lessonTime = CycleTime(day, startTime, endTime)
        } else {
            val date = LocalDate.parse(unparsedDate[1], DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            lessonTime = ScheduledTime(date, startTime, endTime)
        }

        rowData.previousData.prevDay = rowData.row.getCellValue(1)!!
        return Pair(lessonTime, Action.NOTHING)
    }

    private fun getStartAndEndTime(timeCell: List<String>): Pair<String, String> {
        val timeRegex = Regex("[0-9]+:[0-9]+")
        val timeRegexMatches = timeRegex.findAll(timeCell[1])
        val startTime = timeRegexMatches.elementAt(0).value
        val endTime = timeRegexMatches.elementAt(1).value
        return Pair(startTime, endTime)
    }

    private fun getDayOfWeek(str: String): DayOfWeek? {
        return when (str.lowercase()) {
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

    internal enum class Action {
        CONTINUE,
        BREAK,
        NOTHING
    }

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