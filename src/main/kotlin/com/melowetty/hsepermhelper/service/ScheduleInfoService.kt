package com.melowetty.hsepermhelper.service

interface ScheduleInfoService {
    fun getAvailableCourses(): List<Int>
    fun getAvailablePrograms(course: Int): List<String>
    fun getAvailableGroups(course: Int, program: String): List<String>
}