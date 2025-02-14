package com.melowetty.hsepermhelper.excel.impl

import com.melowetty.hsepermhelper.domain.model.lesson.LessonPlace
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.excel.HseTimetableCellExcelParser
import com.melowetty.hsepermhelper.excel.HseTimetableLessonTypeChecker
import com.melowetty.hsepermhelper.excel.model.CellInfo
import com.melowetty.hsepermhelper.excel.model.ExcelLesson
import com.melowetty.hsepermhelper.excel.model.ParsedCellInfo
import com.melowetty.hsepermhelper.excel.model.ParsedLessonInfo
import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.util.LinkUtils
import org.springframework.stereotype.Component

@Component
class HseTimetableCellExcelParserImpl(
    private val lessonTypeChecker: HseTimetableLessonTypeChecker
) : HseTimetableCellExcelParser {
    override fun parseLesson(cellInfo: ParsedCellInfo): List<ExcelLesson> {
        if (!filterCell(cellInfo)) return emptyList()
        return getLesson(
            scheduleInfo = cellInfo.scheduleInfo,
            cell = cellInfo.cellInfo,
        )
    }

    private fun filterCell(cellInfo: ParsedCellInfo): Boolean {
        return !cellInfo.cellInfo.value.lowercase().contains("сессия")
    }

    private fun preProcessingCell(cell: CellInfo): List<List<LessonField>> {
        val fields = getCellAsList(cell.value)
        val fieldsByType = getFieldsByType(fields)
        val rawLessons = getRawLessons(fieldsByType)
        val unmergedLessons = unmergeLessonFields(rawLessons)
        val lessons = clearIncorrectLessonFields(unmergedLessons)
        return checkLessons(lessons)
    }

    private fun getLesson(
        scheduleInfo: ParsedScheduleInfo,
        cell: CellInfo
    ): List<ExcelLesson> {
        val preBuildLessons = preProcessingCell(cell)
        val builtLessons = mutableListOf<ExcelLesson>()
        preBuildLessons.forEach {
            val fields = it.toMutableList()
            val foundSubject = fields.find { it.fieldType == FieldType.SUBJECT }
            val subject: String
            if (foundSubject == null) {
                val link = fields.find { it.fieldType == FieldType.INFO }
                if (link == null) return@forEach
                subject = link.value.replace(LESSON_BUILDING_INFO_REGEX, "").trim()
                fields.add(0, LessonField(subject, FieldType.SUBJECT))
                link.value = link.value.replace(subject, "").trim()
            }
            val additionalLessonInfo = getAdditionalLessonInfo(fields)
            if (additionalLessonInfo.subGroups.isNotEmpty()) {
                additionalLessonInfo.subGroups.forEach { subGroup ->
                    builtLessons.add(
                        buildLesson(
                            fields = fields,
                            additionalLessonInfo = additionalLessonInfo,
                            cell = cell,
                            scheduleInfo = scheduleInfo,
                            subGroup = subGroup
                        )
                    )
                }
            } else {
                builtLessons.add(
                    buildLesson(
                        fields = fields,
                        additionalLessonInfo = additionalLessonInfo,
                        cell = cell,
                        scheduleInfo = scheduleInfo,
                        subGroup = null
                    )
                )
            }
        }
        return builtLessons
    }

    private fun getCellAsList(cellValue: String): List<String> {
        val splitCell = cellValue.split("\n").toMutableList()
        splitCell.removeAll(listOf(""))
        return splitCell
    }

    private fun getFieldsByType(cells: List<String>): List<LessonField> {
        val fields = mutableListOf<LessonField>()
        cells.forEach { cell ->
            var flag = false
            LinkUtils.LINK_REGEX.findAll(cell)
                .forEach {
                    fields.add(LessonField(it.value.trim(), FieldType.LINK))
                    flag = true
                }
            if (LESSON_BUILDING_INFO_REGEX.find(cell) != null) {
                fields.add(LessonField(cell.trim(), FieldType.INFO))
                flag = true
            }
            if (!flag) {
                fields.add(LessonField(cell.trim(), FieldType.SUBJECT))
            }
        }
        return fields
    }

    private fun getRawLessons(fields: List<LessonField>): List<List<LessonField>> {
        val rawLessons = mutableListOf<List<LessonField>>()
        val tempLessonFields = mutableListOf<LessonField>()
        fields
            .forEach {
                if (it.fieldType == FieldType.SUBJECT) {
                    if (tempLessonFields.isNotEmpty()) {
                        rawLessons.add(tempLessonFields.toMutableList())
                    }
                    tempLessonFields.clear()
                    tempLessonFields.add(it)
                } else {
                    tempLessonFields.add(it)
                }
            }
        if (tempLessonFields.isNotEmpty()) {
            rawLessons.add(tempLessonFields.toMutableList())
        }
        return rawLessons
    }

    private fun unmergeLessonFields(fields: List<List<LessonField>>): List<List<LessonField>> {
        val unmergedLessonFields = mutableListOf<List<LessonField>>()
        fields.forEach { lessonFields ->
            val count = lessonFields.count { it.fieldType == FieldType.INFO }
            if (count <= 1) {
                unmergedLessonFields.add(lessonFields)
                return@forEach
            }
            var currentCount = 0
            val tempFields = mutableListOf<LessonField>()
            while (currentCount < count) {
                var i = 0
                var flag = false
                lessonFields.forEach fieldIterator@{ field ->
                    if (field.fieldType == FieldType.INFO) {
                        if (i == currentCount) {
                            tempFields.add(field)
                            flag = true
                        }
                        if (i > currentCount && flag.not()) return@fieldIterator
                        if (i > currentCount && flag) {
                            unmergedLessonFields.add(tempFields.toMutableList())
                            tempFields.clear()
                            flag = false
                            return@fieldIterator
                        }
                        i += 1
                    } else {
                        tempFields.add(field)
                    }
                }
                if (flag) {
                    unmergedLessonFields.add(tempFields.toMutableList())
                    tempFields.clear()
                    flag = false
                }
                currentCount += 1
            }
            if (tempFields.isNotEmpty()) {
                unmergedLessonFields.add(tempFields)
            }
        }
        return unmergedLessonFields
    }

    private fun clearIncorrectLessonFields(allLessonFields: List<List<LessonField>>): List<List<LessonField>> {
        val lessons = allLessonFields.map { it.toMutableList() }.toMutableList()
        if (allLessonFields.size > 1 && allLessonFields.any { lessonFields ->
                lessonFields.all { it.fieldType != FieldType.INFO }
            }) {
            allLessonFields.forEachIndexed { index, lessonFields ->
                if (index != 0) {
                    if (lessonFields.all { it.fieldType != FieldType.INFO }) {
                        lessonFields.forEach {
                            if (it.fieldType == FieldType.SUBJECT) {
                                lessons[index - 1].add(
                                    it.copy(
                                        fieldType = FieldType.ADDITIONAL
                                    )
                                )
                            } else {
                                lessons[index - 1].add(it)
                            }
                        }
                        lessons.remove(lessonFields)
                    }
                }
            }
        }
        return lessons
    }

    private fun checkLessons(allLessons: List<List<LessonField>>): List<List<LessonField>> {
        val lessons = allLessons.map { it.toMutableList() }.toMutableList()
        lessons.forEach {
            if (it.size == 1 && it.first().fieldType == FieldType.INFO) {
                val match = LESSON_BUILDING_INFO_REGEX.containsMatchIn(it.first().value)
                if (match) {
                    val subject = it.first().value
                    val info = LESSON_BUILDING_INFO_REGEX.find(subject)?.value
                    if (info != null) {
                        it.removeAt(0)
                        it.add(LessonField(subject.replace(info, "").trim(), FieldType.SUBJECT))
                        it.add(LessonField(info, FieldType.INFO))
                    }
                }
            }
        }
        return lessons
    }

    private fun buildLesson(
        fields: List<LessonField>,
        cell: CellInfo,
        scheduleInfo: ParsedScheduleInfo,
        additionalLessonInfo: AdditionalLessonInfo,
        subGroup: Int?
    ): ExcelLesson {
        val subject = fields.first { it.fieldType == FieldType.SUBJECT }.value
        val lessonType = lessonTypeChecker.getLessonType(
            ParsedLessonInfo(
                isSessionWeek = scheduleInfo.type == ScheduleType.SESSION_SCHEDULE,
                isUnderlined = cell.isUnderlined,
                subject = subject,
                lessonInfo = additionalLessonInfo.lecturer,
                isHaveBuildingInfo = fields.find { it.fieldType == FieldType.INFO } != null,
                additionalInfo = additionalLessonInfo.additionalInfo,
                schedulePeriod = scheduleInfo.startDate.rangeTo(scheduleInfo.endDate)
            )
        )
        return ExcelLesson(
            subject = lessonType.reformatSubject(subject).removeUselessInfo(),
            lessonType = lessonType,
            places = additionalLessonInfo.places,
            lecturer = additionalLessonInfo.lecturer?.removeUselessInfo(),
            subGroup = subGroup,
            group = cell.group,
            course = cell.course,
            time = cell.time,
            programme = cell.program,
            links = additionalLessonInfo.links,
            additionalInfo = additionalLessonInfo.additionalInfo,
        )
    }

    private fun String.removeUselessInfo(): String {
        return this.replace("(МКД)", "", true).trim()
    }

    private fun getAdditionalLessonInfo(fields: List<LessonField>): AdditionalLessonInfo {
        var lecturer: String? = null
        val placeMatches = mutableListOf<String>()
        val links = mutableListOf<String>()
        val additionalInfo = mutableListOf<String>()
        fields
            .filter { it.fieldType != FieldType.SUBJECT }
            .forEach { field ->
                if (field.fieldType == FieldType.LINK) {
                    links.add(field.value)
                } else if (field.fieldType == FieldType.INFO) {
                    val additionalInfoMatch = ADDITIONAL_INFO_REGEX.find(field.value)
                    if (additionalInfoMatch != null) {
                        val line = field.value.substring(0, additionalInfoMatch.range.first) + field.value.substring(
                            additionalInfoMatch.range.last + 1
                        )
                        if (line.isNotEmpty()) {
                            additionalInfo.add(line)
                        }
                        val additionalInfoRegexGroups = additionalInfoMatch.groups
                        if (additionalInfoRegexGroups.isEmpty().not()) {
                            lecturer = getLecturer(additionalInfoRegexGroups[1]?.value?.trim())?.replace("  ", " ")
                            val placeInfoLine = additionalInfoRegexGroups[2]?.value ?: return@forEach
                            val maybePlaces = placeInfoLine.replace("  ", " ").trim().split(",")
                            placeMatches.addAll(maybePlaces)
                        }
                    }
                } else if (field.fieldType == FieldType.ADDITIONAL) {
                    additionalInfo.add(field.value)
                }
            }
        val places = mutableListOf<LessonPlace>()
        val subgroups = mutableListOf<Int>()
        val offices = mutableListOf<String>()
        placeMatches.forEach { match ->
            val placeMatch = PLACE_INFO_CHECK_REGEX.matches(match)
            if (placeMatch) {
                val match = PLACE_INFO_REGEX.findAll(match).toList()
                val office = match.getOrNull(0)?.value?.trim()
                if (office != null) {
                    offices.add(office)
                }
                val building = match.getOrNull(1)?.value?.toIntOrNull()
                offices.forEach {
                    places.add(
                        LessonPlace(
                            office = it,
                            building = building
                        )
                    )
                }
                offices.clear()
            } else {
                if (match.trim().length in 1..2) {
                    val subgroup = match.trim().toIntOrNull()
                    if (subgroup != null) {
                        subgroups.add(subgroup)
                    } else {
                        offices.add(match.trim())
                    }
                } else {
                    offices.add(match.trim())
                }
            }
        }
        return AdditionalLessonInfo(
            lecturer = lecturer,
            places = if (places.isEmpty()) null else places,
            subGroups = subgroups,
            additionalInfo = if (additionalInfo.isEmpty()) null else additionalInfo,
            links = if (links.isEmpty()) null else links,
        )
    }

    private fun getLecturer(str: String?): String? {
        if (str == null) return null
        if (str.isEmpty()) return null
        return str
    }

    companion object {
        private val LESSON_BUILDING_INFO_REGEX = Regex("\\([^\\(\\)]*\\[\\d*\\].*\\)")
        private val ADDITIONAL_INFO_REGEX = Regex("([^\\/]*)\\((.*)\\)")
        private val PLACE_INFO_REGEX = Regex("\\A[.[^\\[]]+|\\d+")
        private val PLACE_INFO_CHECK_REGEX = Regex("(.+)\\[\\d\\]")
    }

    internal enum class FieldType {
        SUBJECT,
        INFO,
        LINK,
        ADDITIONAL,
    }

    internal data class LessonField(
        var value: String,
        val fieldType: FieldType
    )

    internal data class AdditionalLessonInfo(
        val lecturer: String?,
        val places: List<LessonPlace>?,
        val subGroups: List<Int>,
        val links: List<String>?,
        val additionalInfo: List<String>?,
    )
}