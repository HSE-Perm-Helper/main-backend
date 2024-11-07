package com.melowetty.hsepermhelper.excel.impl

import com.melowetty.hsepermhelper.annotation.Slf4j
import com.melowetty.hsepermhelper.annotation.Slf4j.Companion.log
import com.melowetty.hsepermhelper.excel.HseTimetableCellExcelParser
import com.melowetty.hsepermhelper.excel.HseTimetableSheetExcelParser
import com.melowetty.hsepermhelper.excel.model.CellInfo
import com.melowetty.hsepermhelper.excel.model.ParsedCellInfo
import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.model.CycleTime
import com.melowetty.hsepermhelper.model.Lesson
import com.melowetty.hsepermhelper.model.LessonTime
import com.melowetty.hsepermhelper.model.ScheduleType
import com.melowetty.hsepermhelper.model.ScheduledTime
import com.melowetty.hsepermhelper.notification.ServiceWarnNotification
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.util.RowUtils.Companion.getCellValue
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.springframework.stereotype.Component

@Component
@Slf4j
class HseTimetableSheetExcelParserImpl(
    private val cellParser: HseTimetableCellExcelParser,
    private val notificationService: NotificationService,
) : HseTimetableSheetExcelParser {
    override fun parseSheet(sheet: Sheet, scheduleInfo: ParsedScheduleInfo): List<Lesson> {
        val lessons = mutableListOf<Lesson>()
        val course = getCourse(sheet.sheetName) ?: return listOf()

        val (groups, programs) = fillGroupsAndProgramsFromSheet(sheet)

        val previousData = PreviousData()
        run schedule@{
            for (rowNum in 3 until sheet.lastRowNum) {
                val row = sheet.getRow(rowNum)
                val (parsedLessons, action) = parseRow(
                    RowData(
                        row = row,
                        course = course,
                        programs = programs,
                        groups = groups,
                        scheduleInfo = scheduleInfo,
                        previousData = previousData
                    )
                )

                if (action == Action.BREAK) break
                if (action == Action.CONTINUE) continue

                lessons.addAll(parsedLessons)
            }
        }
        return lessons
    }

    private fun fillGroupsAndProgramsFromSheet(sheet: Sheet): Pair<Map<Int, String>, Map<Int, String>> {
        val groups = mutableMapOf<Int, String>()
        val programs = mutableMapOf<Int, String>()
        for (cellNum in 2 until sheet.getRow(2).physicalNumberOfCells) {
            val group = sheet.getRow(2).getCellValue(cellNum) ?: continue
            if (group != "") {
                if (groups.containsValue(group).not()) {
                    groups[cellNum] = group
                    val programme = getProgramme(group) ?: ""
                    programs[cellNum] = programme
                }
            }
        }
        return Pair(groups, programs)
    }

    private fun parseRow(rowData: RowData): Pair<List<Lesson>, Action> {
        val row = rowData.row

        val lessons = mutableListOf<Lesson>()
        val (lessonTime, action) = getLessonTime(rowData)

        if (action != Action.NOTHING) return Pair(listOf(), action)

        for (cellNum in 2 until row.physicalNumberOfCells) {
            val cell = row.getCell(cellNum)
            val cellValue = cell.stringCellValue
            if (cellValue.isEmpty()) continue

            val lessonInfo = getParsedLessonInfo(cellNum, rowData, lessonTime!!) ?: break

            try {
                val parsedLessons = cellParser.parseLesson(lessonInfo)
                lessons.addAll(parsedLessons)

            } catch (e: Exception) {
                log.error("Произошла ошибка во время обработки пары!")
                log.error(
                    "Расписание: ${rowData.scheduleInfo}, sheet: ${row.sheet.sheetName}, cellAddress: ${cell.address}, value: $cellValue, stacktrace: ",
                    e
                )

                notificationService.sendNotification(
                    ServiceWarnNotification(
                        "Произошла ошибка во время обработки пары!\n" +
                            "Расписание: ${rowData.scheduleInfo}, sheet: ${row.sheet.sheetName}, cellAddress: ${cell.address}, " +
                            "value: $cellValue, stacktrace: ${e.stackTraceToString()}"
                    )
                )
            }
        }

        return Pair(lessons, Action.NOTHING)
    }

    private fun getParsedLessonInfo(cellNum: Int, rowData: RowData, lessonTime: LessonTime): ParsedCellInfo? {
        val group = rowData.groups[cellNum] ?: return null
        val programme = rowData.programs[cellNum] ?: return null
        val isUnderlined = checkIsUnderlined(rowData.row.getCell(cellNum))

        return ParsedCellInfo(
            cellInfo = CellInfo(
                value = rowData.row.getCellValue(cellNum) ?: return null,
                course = rowData.course,
                program = programme,
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
            if (rowData.scheduleInfo.type != ScheduleType.QUARTER_SCHEDULE) {
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
            lessonTime = ScheduledTime(date.dayOfWeek, date, startTime, endTime)
        }

        rowData.previousData.prevDay = rowData.row.getCellValue(1)!!
        return Pair(lessonTime, Action.NOTHING)
    }

    private fun getProgramme(programme: String): String? {
        val programmeRegex = Regex("[А-Яа-яЁёa-zA-Z]+")
        return programmeRegex.find(programme)?.value
    }

    private fun getCourse(sheetName: String): Int? {
        val courseRegex = Regex(pattern = "[0-9]+")
        val course = courseRegex.find(sheetName)?.value ?: return null
        return course.toInt()
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
        val course: Int,
        val groups: Map<Int, String>,
        val programs: Map<Int, String>,
        val previousData: PreviousData
    )

    internal class PreviousData {
        var prevDay = ""
    }
}