package com.melowetty.hsepermhelper.timetable.compose

import com.melowetty.hsepermhelper.timetable.model.EducationType

interface ParentTimetableInfo {
    fun getCourses(): Map<EducationType, List<Int>>
    fun getPrograms(): Map<Pair<EducationType, Int>, List<String>>
    fun getGroups(): Map<Triple<EducationType, Int, String>, List<String>>
    fun getAllGroups(): Map<String, EducationType>
}