package com.melowetty.hsepermhelper.repository.impl

import Schedule
import com.melowetty.hsepermhelper.events.common.EventType
import com.melowetty.hsepermhelper.events.internal.ScheduleChangedEvent
import com.melowetty.hsepermhelper.events.internal.ScheduleFilesChangedEvent
import com.melowetty.hsepermhelper.exceptions.ScheduleNotFoundException
import com.melowetty.hsepermhelper.models.ChangedSchedule
import com.melowetty.hsepermhelper.models.Lesson
import com.melowetty.hsepermhelper.models.LessonType
import com.melowetty.hsepermhelper.models.ScheduleType
import com.melowetty.hsepermhelper.repository.ScheduleRepository
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import org.apache.poi.ss.usermodel.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.io.InputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component
class ScheduleRepositoryImpl(
    private val eventPublisher: ApplicationEventPublisher,
    private val scheduleFilesService: ScheduleFilesService
): ScheduleRepository {
    private var schedules = mutableListOf<Schedule>()

    override fun getSchedules(): List<Schedule> {
        return schedules
    }

    @EventListener
    fun handleScheduleFilesUpdate(event: ScheduleFilesChangedEvent) {
        fetchSchedules()
    }

    override fun fetchSchedules(): List<Schedule> {
        val newSchedules = mutableListOf<Schedule>()
        scheduleFilesService.getScheduleFiles()
            .forEach {
                val schedule = parseSchedule(it.file)
                if(schedule != null) newSchedules.add(schedule)
            }
        val changes = mutableMapOf<EventType, List<ChangedSchedule>>()
        val mappedSchedules = schedules.map { it.weekStart }
        for (newSchedule in newSchedules) {
            val existsSchedule = schedules.find { it.weekStart == newSchedule.weekStart }
            if(existsSchedule != null && existsSchedule.lessons != newSchedule.lessons) {
                val editedSchedules: MutableList<ChangedSchedule> = changes.getOrDefault(EventType.EDITED, listOf()).toMutableList()
                editedSchedules.add(
                    ChangedSchedule(
                    before = existsSchedule,
                    after = newSchedule
                )
                )
                changes[EventType.EDITED] = editedSchedules
            }
            else if(mappedSchedules.contains(newSchedule.weekStart).not()) {
                val addedSchedules: MutableList<ChangedSchedule> = changes.getOrDefault(EventType.ADDED, listOf()).toMutableList()
                addedSchedules.add(ChangedSchedule(
                    before = null,
                    after = newSchedule,
                ))
                changes[EventType.ADDED] = addedSchedules
            }
        }
        val deletedSchedules = schedules
            .filter { schedule ->
                newSchedules.map { it.weekStart }.contains(schedule.weekStart).not()
            }
        changes[EventType.DELETED] = deletedSchedules.map { ChangedSchedule(before = it, after = null) }
        val event = ScheduleChangedEvent(
            changes = changes
        )
        schedules = newSchedules
        eventPublisher.publishEvent(event)
        return schedules
    }

    override fun getAvailableCourses(): List<Int> {
        if(schedules.isEmpty()) throw ScheduleNotFoundException("Расписание не найдено!")
        val courses = schedules.flatMap { it.lessons.values }
            .asSequence()
            .flatten()
            .map { it.course }
            .toSortedSet()
            .toList()
        if(courses.isEmpty()) throw RuntimeException("Возникли проблемы с обработкой расписания!")
        return courses
    }

    override fun getAvailablePrograms(course: Int): List<String> {
        if(schedules.isEmpty()) throw ScheduleNotFoundException("Расписание не найдено!")
        val programs = schedules.flatMap { it.lessons.values }
            .asSequence()
            .flatten()
            .filter { it.course == course }
            .map { it.programme }
            .toSortedSet()
            .toList()
        if(programs.isEmpty()) throw IllegalArgumentException("Курс не найден в расписании!")
        return programs
    }

    override fun getAvailableGroups(course: Int, program: String): List<String> {
        if(schedules.isEmpty()) throw ScheduleNotFoundException("Расписание не найдено!")
        val groups = schedules.flatMap { it.lessons.values }
            .asSequence()
            .flatten()
            .filter { it.course == course && it.programme == program }
            .map { it.group }
            .toSortedSet()
            .toList()
        if(groups.isEmpty()) throw IllegalArgumentException("Программа не найдена в расписании!")
        return groups
    }

    override fun getAvailableSubgroups(course: Int, program: String, group: String): List<Int> {
        if(schedules.isEmpty()) throw ScheduleNotFoundException("Расписание не найдено!")
        val groups = getAvailableGroups(course, program)
        if(groups.isEmpty()) throw IllegalArgumentException("Группа не найдена в расписании!")
        val groupNumRegex = Regex("[А-Яа-яЁёa-zA-Z]+-\\d*-(\\d*)")
        try {
            val matches = groupNumRegex.find(groups.last())
            val lastGroupNumMatch = matches!!.groups[1]
            val lastGroupNum = lastGroupNumMatch!!.value.toInt()
            return (1..lastGroupNum * 2).toList()
        } catch (e: Exception) {
            throw RuntimeException("Возникли проблемы с обработкой группы!")
        }
    }

    private fun getWorkbook(inputStream: InputStream): Workbook {
        return WorkbookFactory.create(inputStream)
    }

    private fun getProgramme(programme: String): String {
        val programmeRegex = Regex("[А-Яа-яЁёa-zA-Z]+")
        return programmeRegex.find(programme)?.value ?: ""
    }

    private fun getCourse(sheetName: String): Int {
        val courseRegex = Regex(pattern = "[0-9]+")
        return (courseRegex.find(sheetName)?.value ?: "0").toInt()
    }

    private fun parseSchedule(inputStream: InputStream): Schedule? {
        try {
            val workbook = getWorkbook(inputStream)
            val lessonsList = mutableListOf<Lesson>()
            val scheduleInfo = getWeekInfo(getValue(
                workbook.getSheetAt(1),
                workbook.getSheetAt(1).getRow(1).getCell(3))
            )
            if (scheduleInfo.weekStartDate == null || scheduleInfo.weekEndDate == null) {
                return null
            }
            for (i in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(i)
                if (sheet.sheetName.lowercase() == "доц") continue
                val course = getCourse(sheet.sheetName)
                val groups = mutableMapOf<Int, String>()
                val programs = mutableMapOf<Int, String>()
                for (cellNum in 2 until sheet.getRow(2).physicalNumberOfCells) {
                    val group = getValue(sheet, sheet.getRow(2).getCell(cellNum))
                    if (group != "") {
                        groups[cellNum] = group
                        val programme = getProgramme(group)
                        programs[cellNum] = programme
                    }
                }
                run schedule@ {
                    for(rowNum in 3 until sheet.lastRowNum) {
                        val row = sheet.getRow(rowNum)
                        val unparsedDate = getValue(sheet, row.getCell(0)).split("\n")
                        val dates = mutableListOf<LocalDate>()
                        if (unparsedDate.size < 2) {
                            if(scheduleInfo.scheduleType != ScheduleType.QUARTER_SCHEDULE) break
                            val day = getDayOfWeek(unparsedDate[0]) ?: continue
                            var dateIteration = scheduleInfo.weekStartDate.plusDays(
                                day.ordinal.toLong() - scheduleInfo.weekStartDate.dayOfWeek.ordinal
                            )
                            while(dateIteration.isAfter(scheduleInfo.weekEndDate).not()) {
                                if (dateIteration.isBefore(LocalDate.now())) {
                                    dateIteration = dateIteration.plusDays(7)
                                    continue
                                }
                                dates.add(dateIteration)
                                dateIteration = dateIteration.plusDays(7)
                            }
                        } else {
                            dates.add(LocalDate.parse(unparsedDate[1], DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                        }
                        val time = getValue(sheet, row.getCell(1)).split("\n")[2]
                        val timeRegex = Regex("[0-9]+:[0-9]+")
                        val timeRegexMatches = timeRegex.findAll(time)
                        val startTime = timeRegexMatches.elementAt(0).value
                        val splitStartTime = startTime.split(":")
                        val startLocalTime = LocalTime.of(splitStartTime[0].toInt(), splitStartTime[1].toInt())
                        val endTime = timeRegexMatches.elementAt(1).value
                        val splitEndTime = endTime.split(":")
                        val endLocalTime = LocalTime.of(splitEndTime[0].toInt(), splitEndTime[1].toInt())
                        run line@ {
                            for (cellNum in 2 until row.physicalNumberOfCells) {
                                val cell = row.getCell(cellNum)
                                val cellValue = getValue(sheet = sheet, cell = cell)
                                if(cellValue.isEmpty()) continue
                                val group = groups.getOrDefault(cellNum, "")
                                if (group == "") {
                                    return@line
                                }
                                val programme = programs.getOrDefault(cellNum, "N/a")
                                val font = workbook.getFontAt(cell.cellStyle.fontIndex)
                                val isUnderlined = font.underline != Font.U_NONE
                                try {
                                    for (date in dates) {
                                        val startLocalDateTime = LocalDateTime.of(date, startLocalTime)
                                        val endLocalDateTime = LocalDateTime.of(date, endLocalTime)
                                        val lessons = getLesson(
                                            scheduleInfo = scheduleInfo,
                                            cell = CellInfo(
                                                value = cellValue,
                                                isUnderlined = isUnderlined,
                                            ),
                                            serviceLessonInfo = ServiceLessonInfo(
                                                course = course,
                                                programme = programme,
                                                group = group,
                                                date = date,
                                                startTimeStr = startTime,
                                                endTimeStr = endTime,
                                                startTime = startLocalDateTime,
                                                endTime = endLocalDateTime,
                                            )
                                        )
                                        for (lesson in lessons) {
                                            lessonsList.add(lesson)
                                        }
                                    }
                                } catch (e: Exception) {
                                    println("Произошла ошибка во время обработки пары!")
                                    println("cell: ${cellValue}")
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
            lessonsList.sort()
            return Schedule(
                weekNumber = scheduleInfo.weekNumber,
                weekStart = scheduleInfo.weekStartDate,
                weekEnd = scheduleInfo.weekEndDate,
                lessons = lessonsList
                    .groupBy {
                    it.date
                },
                scheduleType = scheduleInfo.scheduleType,

            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            println("Произошла ошибка во время обработки файла с расписанием!")
            return null
        }
    }

    private fun getValue(sheet: Sheet, cell: Cell?): String {
        if (cell == null) return ""
        for(region in sheet.mergedRegions){
            if(region.isInRange(cell)) {
                return sheet.getRow(region.firstRow).getCell(region.firstColumn).stringCellValue
            }
        }
        return cell.stringCellValue ?: ""
    }

    private fun getLesson(
        scheduleInfo: ScheduleInfo,
        serviceLessonInfo: ServiceLessonInfo,
        cell: CellInfo
    ): List<Lesson> {
        val splitCell = cell.value.split("\n").toMutableList()
        splitCell.removeAll(listOf(""))
        val fields = getFieldsByType(splitCell)
        val rawLessons = getRawLessons(fields)
        val unmergedLessons = unmergeLessonFields(rawLessons)
        val lessons = clearIncorrectLessonFields(unmergedLessons)
        val builtLessons = lessons.map {
            val additionalLessonInfo = getAdditionalLessonInfo(it)
            buildLesson(
                fields = it,
                serviceLessonInfo = serviceLessonInfo,
                additionalLessonInfo = additionalLessonInfo,
                cell = cell,
                scheduleInfo = scheduleInfo,
            )
        }
        return builtLessons
    }

    private fun getFieldsByType(cells: List<String>): List<LessonField> {
        val fields = mutableListOf<LessonField>()
        cells.forEach { cell ->
            var flag = false
            LINK_REGEX.findAll(cell)
                .forEach {
                    fields.add(LessonField(it.value.trim(), FieldType.LINK))
                    flag = true
                }
            if(LESSON_BUILDING_INFO_REGEX.find(cell) != null) {
                fields.add(LessonField(cell.trim(), FieldType.INFO))
                flag = true
            }
            if(!flag) {
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
                    if(tempLessonFields.isNotEmpty()) {
                        rawLessons.add(tempLessonFields.toMutableList())
                    }
                    tempLessonFields.clear()
                    tempLessonFields.add(it)
                } else {
                    tempLessonFields.add(it)
                }
            }
        if(tempLessonFields.isNotEmpty()) {
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
            while(currentCount < count) {
                var i = 0
                var flag = false
                lessonFields.forEach fieldIterator@{ field ->
                    if(field.fieldType == FieldType.INFO) {
                        if (i == currentCount) {
                            tempFields.add(field)
                            flag = true
                        }
                        if(i > currentCount && flag.not()) return@fieldIterator
                        if(i > currentCount && flag) {
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
                if(flag) {
                    unmergedLessonFields.add(tempFields.toMutableList())
                    tempFields.clear()
                    flag = false
                }
                currentCount += 1
            }
            if(tempFields.isNotEmpty()) {
                unmergedLessonFields.add(tempFields)
            }
        }
        return unmergedLessonFields
    }

    private fun clearIncorrectLessonFields(allLessonFields: List<List<LessonField>>): List<List<LessonField>> {
        val lessons = allLessonFields.map { it.toMutableList() }.toMutableList()
        if(allLessonFields.size > 1 && allLessonFields.any { lessonFields ->
                lessonFields.all { it.fieldType == FieldType.SUBJECT }
            }) {
            allLessonFields.forEachIndexed { index, lessonFields ->
                if(index != 0) {
                    if (lessonFields.all { it.fieldType == FieldType.SUBJECT }) {
                        lessonFields.forEach {
                            lessons[index - 1].add(
                                it.copy(
                                    fieldType = FieldType.ADDITIONAL
                                )
                            )
                        }
                        lessons.remove(lessonFields)
                    }
                }
            }
        }
        return lessons
    }

    private fun buildLesson(
        fields: List<LessonField>,
        cell: CellInfo,
        scheduleInfo: ScheduleInfo,
        serviceLessonInfo: ServiceLessonInfo,
        additionalLessonInfo: AdditionalLessonInfo,
    ): Lesson {
        val subject = fields.first { it.fieldType == FieldType.SUBJECT }.value.trim()
        val lessonType = getLessonType(
            isSessionWeek = scheduleInfo.scheduleType == ScheduleType.SESSION_WEEK_SCHEDULE,
            isUnderlined = cell.isUnderlined,
            subject = subject,
            lessonInfo = additionalLessonInfo.lecturer,
        )
        return Lesson(
            subject = subject,
            lessonType = lessonType,
            building = additionalLessonInfo.building,
            office =  additionalLessonInfo.office,
            lecturer = additionalLessonInfo.lecturer,
            subGroup = additionalLessonInfo.subGroup,
            group =  serviceLessonInfo.group,
            course = serviceLessonInfo.course,
            date = serviceLessonInfo.date,
            programme = serviceLessonInfo.programme,
            startTime = serviceLessonInfo.startTime,
            startTimeStr = serviceLessonInfo.startTimeStr,
            endTime = serviceLessonInfo.endTime,
            endTimeStr = serviceLessonInfo.endTimeStr,
            parentScheduleType = scheduleInfo.scheduleType,
            links = additionalLessonInfo.links,
            additionalInfo = additionalLessonInfo.additionalInfo,
        )
    }

    private fun getAdditionalLessonInfo(fields: List<LessonField>): AdditionalLessonInfo {
        var lecturer: String? = null
        var office: String? = null
        var building: Int? = null
        var subgroup: Int? = null
        val links = mutableListOf<String>()
        val additionalInfo = mutableListOf<String>()
        fields
            .filter { it.fieldType != FieldType.SUBJECT }
            .forEach { field ->
                if(field.fieldType == FieldType.LINK) {
                    links.add(field.value)
                } else if(field.fieldType == FieldType.INFO) {
                    val additionalInfoRegexGroups = ADDITIONAL_INFO_REGEX.find(field.value)?.groups
                    if(additionalInfoRegexGroups != null && additionalInfoRegexGroups.isEmpty().not()) {
                        lecturer = getLecturer(additionalInfoRegexGroups[1]?.value?.trim())?.replace("  ", " ")
                        val placeInfoLine = additionalInfoRegexGroups[2]?.value ?: return@forEach
                        val placeInfoMatches = PLACE_INFO_REGEX.findAll(placeInfoLine).toList()
                        office = placeInfoMatches.getOrNull(0)?.value?.trim()
                        building = placeInfoMatches.getOrNull(1)?.value?.toIntOrNull()
                        subgroup = placeInfoMatches.getOrNull(2)?.value?.toIntOrNull()
                    }
                } else if(field.fieldType == FieldType.ADDITIONAL) {
                    additionalInfo.add(field.value)
                }
            }
        return AdditionalLessonInfo(
            lecturer = lecturer,
            office = office,
            building = building,
            subGroup = subgroup,
            additionalInfo = if (additionalInfo.isEmpty()) null else additionalInfo,
            links = if (links.isEmpty()) null else links,
        )
    }

    private fun getLessonType(
        isSessionWeek: Boolean,
        subject: String,
        lessonInfo: String? = "",
        isUnderlined: Boolean,
    ): LessonType {
        val pureSubject = subject.lowercase()
        val pureLessonInfo = lessonInfo?.lowercase()
        if (pureSubject.contains("(ведомост")) return LessonType.STATEMENT
        if (pureSubject.contains("независимый экзамен")) return LessonType.INDEPENDENT_EXAM
        if (pureSubject.contains("экзамен")) return LessonType.EXAM
        if (pureSubject.contains("зачёт") || pureSubject.contains("зачет")) return LessonType.TEST
        if (pureSubject.contains("английский язык")) return LessonType.COMMON_ENGLISH
        if (pureSubject.contains("майнор")) {
            if (isSessionWeek) return LessonType.EXAM
            return LessonType.COMMON_MINOR
        }
        if (pureSubject == "практика") return LessonType.PRACTICE
        if (pureLessonInfo?.contains("мкд") == true) return LessonType.ICC
        if (pureSubject.contains("лекция") || pureSubject.contains("лекции")) return LessonType.LECTURE
        if (pureSubject.contains("семинар") || pureSubject.contains("семинары")) return LessonType.SEMINAR
        if (pureSubject.contains("доц по выбору")) return LessonType.UNDEFINED_AED
        if (pureSubject.contains("доц")) return LessonType.AED
        if (isUnderlined) return LessonType.LECTURE
        return LessonType.SEMINAR
    }

    private fun getWeekInfo(weekInfoStr: String): ScheduleInfo {
        val weekInfoRegex = Regex("\\D*(\\d*).+\\s+(\\d+\\.\\d+\\.\\d+)\\s.+\\s(\\d+\\.\\d+\\.\\d+)")
        val weekInfoMatches = weekInfoRegex.findAll(weekInfoStr)
        val weekInfoGroups = weekInfoMatches.elementAt(0).groups
        val weekNumber = (weekInfoGroups.get(1)?.value?.strip())?.toIntOrNull()
        val datePattern = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val weekStart = LocalDate.parse(weekInfoGroups.get(2)?.value, datePattern)
        val weekEnd = LocalDate.parse(weekInfoGroups.get(3)?.value, datePattern)
        var scheduleType = ScheduleType.COMMON_WEEK_SCHEDULE
        if(weekNumber == null) {
            scheduleType = ScheduleType.SESSION_WEEK_SCHEDULE
        } else if(weekEnd.toEpochDay() - weekStart.toEpochDay() > 7) {
            scheduleType = ScheduleType.QUARTER_SCHEDULE
        }
        return ScheduleInfo(
            weekNumber = weekNumber,
            weekStartDate = weekStart,
            weekEndDate = weekEnd,
            scheduleType = scheduleType,
        )
    }

    private fun getLecturer(str: String?): String? {
        if (str == null) return null
        if (str.isEmpty()) return null
        return str
    }

    private fun getDayOfWeek(str: String): DayOfWeek? {
        return when(str.lowercase()) {
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

    companion object {
        private val LESSON_BUILDING_INFO_REGEX = Regex("[^МКД|ДОЦ]\\(.+\\[\\d*\\].*\\)")
        private val LINK_REGEX = Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
        private val ADDITIONAL_INFO_REGEX = Regex("([^\\/]*)\\((.*)\\)")
        private val PLACE_INFO_REGEX = Regex("\\A[.[^\\[]]+|\\d+")
    }

    internal data class ScheduleInfo(
        val weekNumber: Int?,
        val weekStartDate: LocalDate?,
        val weekEndDate: LocalDate?,
        val scheduleType: ScheduleType,
    )

    internal enum class FieldType {
        SUBJECT,
        INFO,
        LINK,
        ADDITIONAL,
    }
    internal data class LessonField(
        val value: String,
        val fieldType: FieldType
    )

    internal data class CellInfo(
        val value: String,
        val isUnderlined: Boolean,
    )

    internal data class ServiceLessonInfo(
        val course: Int,
        val programme: String,
        val group: String,
        val date: LocalDate,
        val startTimeStr: String,
        val endTimeStr: String,
        val startTime: LocalDateTime,
        val endTime: LocalDateTime,
    )

    internal data class AdditionalLessonInfo(
        val lecturer: String?,
        val office: String?,
        val building: Int?,
        val subGroup: Int?,
        val links: List<String>?,
        val additionalInfo: List<String>?,
    )
}