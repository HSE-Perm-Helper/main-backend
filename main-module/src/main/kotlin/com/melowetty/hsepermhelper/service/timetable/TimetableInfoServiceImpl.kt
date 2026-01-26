package com.melowetty.hsepermhelper.service.timetable

import com.melowetty.hsepermhelper.domain.model.CacheWrapper
import com.melowetty.hsepermhelper.service.TimetableInfoService
import com.melowetty.hsepermhelper.timetable.compose.ParentTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.EducationType
import org.springframework.stereotype.Service

@Service
class TimetableInfoServiceImpl(
    private val timetablesInfo: List<ParentTimetableInfo>,
) : TimetableInfoService {
    private val coursesByEduType = CacheWrapper {
        timetablesInfo.map { it.getCourses() }.reduce { acc, map -> acc + map }
    }

    private val programsByEduTypeAndCourse = CacheWrapper {
        timetablesInfo.map { it.getPrograms() }.reduce { acc, map -> acc + map }
    }

    private val groupsByEduTypeAndCourseAndProgram = CacheWrapper {
        timetablesInfo.map { it.getGroups() }.reduce { acc, map -> acc + map }
    }

    private val educationTypeByGroup = CacheWrapper {
        timetablesInfo.map { it.getAllGroups() }.reduce { acc, map -> acc + map }
    }

    override fun getAvailableEducationTypes(): List<EducationType> {
        return EducationType.entries.toList().sortedBy { it.ordinal }
    }

    override fun getAvailableCourses(educationType: EducationType): List<Int> {
        return coursesByEduType.get()[educationType] ?: emptyList()
    }

    override fun getAvailablePrograms(educationType: EducationType, course: Int): List<String> {
        return programsByEduTypeAndCourse.get()[Pair(educationType, course)] ?: emptyList()
    }

    override fun getAvailableGroups(educationType: EducationType, course: Int, program: String): List<String> {
        return groupsByEduTypeAndCourseAndProgram.get()[Triple(educationType, course, program)] ?: emptyList()
    }

    override fun getEducationTypeByGroup(group: String): EducationType {
        return educationTypeByGroup.get()[group] ?: EducationType.BACHELOR_OFFLINE
    }
}