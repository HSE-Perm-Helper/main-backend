package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.timetable.model.EducationType

interface TimetableInfoService {
    fun getAvailableEducationTypes(): List<EducationType>
    fun getAvailableCourses(educationType: EducationType): List<Int>
    fun getAvailablePrograms(educationType: EducationType, course: Int): List<String>
    fun getAvailableGroups(educationType: EducationType, course: Int, program: String): List<String>
    fun getEducationTypeByGroup(group: String): EducationType
}