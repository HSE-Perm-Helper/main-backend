package com.melowetty.hsepermhelper.controllers

import Schedule
import com.melowetty.hsepermhelper.models.Response
import com.melowetty.hsepermhelper.models.ScheduleFileLinks
import com.melowetty.hsepermhelper.service.ScheduleService
import com.melowetty.hsepermhelper.utils.UrlUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Tag(name = "Расписание", description = "Взаимодействие с расписанием")
@RestController
class ScheduleController(
    private val scheduleService: ScheduleService,
) {
    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение расписания пользователя",
        description = "Позволяет получить расписания пользователя по его Telegram ID"
    )
    @GetMapping(
        "schedule/{telegramId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getSchedule(
        @Parameter(description = "Telegram ID пользователя")
        @PathVariable("telegramId")
        telegramId: Long,
    ): Response<List<Schedule>> {
        return Response(scheduleService.getUserSchedulesByTelegramId(telegramId))
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение расписания пользователя на 2 недели в виде файла",
        description = "Позволяет получить расписание на 2 недели в виде файла для пользователя по его Telegram ID"
    )
    @GetMapping(
        "schedule/{telegramId}/download",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getScheduleFile(
        @Parameter(description = "Telegram ID пользователя")
        @PathVariable telegramId: Long,
        request: HttpServletRequest
    ): Response<ScheduleFileLinks> {
        val baseUrl = UrlUtils.getBaseUrl(request)
        return Response(scheduleService.getScheduleFileByTelegramId(baseUrl, telegramId))
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение доступных для выбора курсов",
        description = "Позволяет получить доступные для выбора курсы для регистрации"
    )
    @GetMapping(
        "schedule/available_courses",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailableCourses(
    ): Response<List<Int>> {
        return Response(scheduleService.getAvailableCourses())
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение доступных для выбора проргамм",
        description = "Позволяет получить доступные для выбора программ для регистрации",
    )
    @GetMapping(
        "schedule/available_programs",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailablePrograms(
        @Parameter(description = "Номер курса")
        @RequestParam("course")
        course: Int,
    ): Response<List<String>> {
        return Response(scheduleService.getAvailablePrograms(course))
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение доступных для выбора групп",
        description = "Позволяет получить доступные для выбора группы для регистрации",
    )
    @GetMapping(
        "schedule/available_groups",
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
        return Response(scheduleService.getAvailableGroups(course, program))
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение доступных для выбора подгрупп",
        description = "Позволяет получить доступные для выбора подгруппы для регистрации"
    )
    @GetMapping(
        "schedule/available_subgroups",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailableSubgroups(
        @Parameter(description = "Номер курса")
        @RequestParam("course")
        course: Int,
        @Parameter(description = "Образовательная программа")
        @RequestParam("program")
        program: String,
        @Parameter(description = "Группа студента")
        @RequestParam("group")
        group: String
    ): Response<List<Int>> {
        return Response(scheduleService.getAvailableSubgroups(course, program, group))
    }
}