package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.service.ExcelScheduleService
import com.melowetty.hsepermhelper.service.ScheduleInfoService
import org.springframework.stereotype.Service

@Service
class ScheduleInfoServiceImpl(
    private val excelScheduleService: ExcelScheduleService,
) : ScheduleInfoService {
    override fun getAvailableCourses(): List<Int> {
        return excelScheduleService.getAvailableCourses()
    }

    override fun getAvailablePrograms(course: Int): List<String> {
        return excelScheduleService.getAvailablePrograms(course = course)
    }

    override fun getAvailableGroups(course: Int, program: String): List<String> {
        return excelScheduleService.getAvailableGroups(course = course, program = program)
    }
}