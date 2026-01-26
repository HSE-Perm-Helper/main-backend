package com.melowetty.hsepermhelper.timetable.compose.impl

import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.timetable.compose.ParentTimetable
import com.melowetty.hsepermhelper.persistence.storage.ExcelTimetableStorage
import com.melowetty.hsepermhelper.timetable.compose.ParentTimetableInfo
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.TimetableLessonsUtils
import com.melowetty.hsepermhelper.domain.model.timetable.EducationType
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetable
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableInfo
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableSource
import org.springframework.stereotype.Component

@Component
class ExcelParentTimetable(
    private val storage: ExcelTimetableStorage
) : ParentTimetable, ParentTimetableInfo {
    override fun getTimetables(user: UserRecord): List<InternalTimetableInfo> {
        return storage.getParentTimetables(user.educationGroup.educationType)
    }

    override fun get(id: String, user: UserRecord): InternalTimetable {
        return storage.getTimetableFilteredByGroup(id, user.educationGroup.group)
    }

    override fun getProcessorType(): InternalTimetableSource {
        return InternalTimetableSource.EXCEL
    }

    override fun isAvailableForUser(user: UserRecord): Boolean {
        return true
    }

    override fun getCourses(): Map<EducationType, List<Int>> {
        return storage.getAllTimetablesGroups().map { entry ->
            entry.key to entry.value.map { group ->
                TimetableLessonsUtils.extractCourse(group)
            }.distinct().sorted()
        }.toMap()
    }

    override fun getPrograms(): Map<Pair<EducationType, Int>, List<String>> {
        return storage.getAllTimetablesGroups().flatMap { (eduType, groups) ->
            val groupedByCourse = groups.groupBy { TimetableLessonsUtils.extractCourse(it) }
            groupedByCourse.map { (course, courseGroups) ->
                val programs = courseGroups
                    .map { TimetableLessonsUtils.extractProgram(it) }
                    .distinct()
                    .sorted()
                (eduType to course) to programs
            }
        }.toMap()
    }

    override fun getGroups(): Map<Triple<EducationType, Int, String>, List<String>> {
        return storage.getAllTimetablesGroups().flatMap { (eduType, groups) ->
            groups.map { group ->
                val course = TimetableLessonsUtils.extractCourse(group)
                val program = TimetableLessonsUtils.extractProgram(group)
                Triple(eduType, course, program) to group
            }
        }.groupBy { it.first }
            .mapValues { entry ->
                entry.value.map { it.second }.sorted()
            }
    }

    override fun getAllGroups(): Map<String, EducationType> {
        return storage.getAllTimetablesGroups().map { entry ->
            entry.value.map { it to entry.key }
        }.flatten().toMap()
    }
}