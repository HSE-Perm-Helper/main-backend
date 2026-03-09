package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.online

import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.LessonTypeUtils
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedCellInfo
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedLessonInfo
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableType
import com.melowetty.hsepermhelper.domain.model.timetable.impl.GroupBasedLesson
import com.melowetty.hsepermhelper.util.LinkUtils

object OnlineTimetableCellParser {

    private val EMBEDDED_LECTURER_REGEX = Regex("[А-ЯЁ][а-яё]+\\s+[А-ЯЁ]\\.[А-ЯЁ]\\.")

    fun parseLesson(cellInfo: ParsedCellInfo): List<GroupBasedLesson> {
        val lines = cellInfo.cellInfo.value.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return emptyList()

        var subject = lines[0]
        if (subject.lowercase().contains("сессия")) return emptyList()

        var lecturer: String? = null
        val links = mutableListOf<String>()

        for (line in lines.drop(1)) {
            if (LinkUtils.LINK_REGEX.containsMatchIn(line)) {
                links.add(line)
            } else if (lecturer == null) {
                lecturer = line
            }
        }

        if (lecturer == null) {
            val match = EMBEDDED_LECTURER_REGEX.find(subject)
            if (match != null) {
                lecturer = match.value.trim()
                subject = subject.substring(0, match.range.first).trim()
            }
        }

        val lessonType = LessonTypeUtils.getLessonType(
            ParsedLessonInfo(
                isSessionWeek = cellInfo.scheduleInfo.type == InternalTimetableType.BACHELOR_SESSION_TIMETABLE,
                isUnderlined = cellInfo.cellInfo.isUnderlined,
                subject = subject,
                lessonInfo = lecturer,
                isHaveBuildingInfo = lecturer != null,
                additionalInfo = null,
                schedulePeriod = cellInfo.scheduleInfo.startDate.rangeTo(cellInfo.scheduleInfo.endDate),
            )
        )

        return listOf(
            GroupBasedLesson(
                subject = subject,
                lessonType = lessonType,
                places = null,
                lecturer = lecturer,
                group = cellInfo.cellInfo.group,
                subGroup = null,
                time = cellInfo.cellInfo.time,
                links = links.ifEmpty { null },
                additionalInfo = null,
            )
        )
    }
}
