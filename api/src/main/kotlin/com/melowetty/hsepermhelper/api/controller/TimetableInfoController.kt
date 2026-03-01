package com.melowetty.hsepermhelper.controller

import com.melowetty.hsepermhelper.domain.model.Response
import com.melowetty.hsepermhelper.service.TimetableInfoService
import com.melowetty.hsepermhelper.domain.model.timetable.EducationType
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/timetable-info")
class TimetableInfoController (
    private val timetableInfoService: TimetableInfoService
){
    @GetMapping(
        "education-types",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailableEducationTypes(): Response<List<EducationType>> {
        return Response(timetableInfoService.getAvailableEducationTypes())
    }

    @GetMapping(
        "courses",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailableCourses(
        @RequestParam("education_type") educationType: EducationType = EducationType.BACHELOR_OFFLINE,
    ): Response<List<Int>> {
        return Response(timetableInfoService.getAvailableCourses(educationType))
    }

    @GetMapping(
        "programs",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailablePrograms(
        @RequestParam("education_type") educationType: EducationType = EducationType.BACHELOR_OFFLINE,
        @Parameter(description = "Номер курса")
        @RequestParam("course")
        course: Int,
    ): Response<List<String>> {
        return Response(timetableInfoService.getAvailablePrograms(educationType, course))
    }

    @GetMapping(
        "groups",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailableGroups(
        @RequestParam("education_type") educationType: EducationType = EducationType.BACHELOR_OFFLINE,
        @Parameter(description = "Номер курса")
        @RequestParam("course")
        course: Int,
        @Parameter(description = "Образовательная программа")
        @RequestParam("program")
        program: String,
    ): Response<List<String>> {
        return Response(timetableInfoService.getAvailableGroups(educationType, course, program))
    }
}