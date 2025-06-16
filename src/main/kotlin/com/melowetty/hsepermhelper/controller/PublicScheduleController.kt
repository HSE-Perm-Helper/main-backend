package com.melowetty.hsepermhelper.controller

import com.melowetty.hsepermhelper.domain.model.Response
import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.service.PersonalScheduleService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/schedule")
class PublicScheduleController(
    private val personalScheduleService: PersonalScheduleService,
) {
    @GetMapping
    fun getScheduleByGroup(
        @Parameter(description = "Группа")
        @RequestParam("group")
        group: String,
    ): Response<List<Lesson>> {
        return Response(personalScheduleService.getScheduleByGroup(group))
    }

    @GetMapping(
        "available_courses",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailableCourses(
    ): Response<List<Int>> {
        return Response(personalScheduleService.getAvailableCourses())
    }

    @GetMapping(
        "available_programs",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailablePrograms(
        @Parameter(description = "Номер курса")
        @RequestParam("course")
        course: Int,
    ): Response<List<String>> {
        return Response(personalScheduleService.getAvailablePrograms(course))
    }

    @GetMapping(
        "available_groups",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailableGroups(
        @Parameter(description = "Номер курса")
        @RequestParam("course")
        course: Int,
        @Parameter(description = "Образовательная программа")
        @RequestParam("program")
        program: String,
    ): Response<List<String>> {
        return Response(personalScheduleService.getAvailableGroups(course, program))
    }
}