package com.melowetty.hsepermhelper.controllers

import com.melowetty.hsepermhelper.models.Response
import com.melowetty.hsepermhelper.models.ScheduleFileLinks
import com.melowetty.hsepermhelper.models.UserEventType
import com.melowetty.hsepermhelper.models.Schedule
import com.melowetty.hsepermhelper.service.ScheduleService
import com.melowetty.hsepermhelper.service.UserEventService
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
    private val userEventService: UserEventService,
) {
    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение расписания пользователя",
        description = "Позволяет получить расписания пользователя по его Telegram ID"
    )
    @GetMapping(
        "v2/schedule/{telegramId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getSchedule(
        @Parameter(description = "Telegram ID пользователя")
        @PathVariable("telegramId")
        telegramId: Long,
    ): Response<List<Schedule>> {
        userEventService.addUserEvent(telegramId, UserEventType.GET_SCHEDULE)
        return Response(scheduleService.getUserSchedulesByTelegramId(telegramId))
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение всех расписаний пользователя в виде файла типа ICS",
        description = "Позволяет получить все расписания пользователя в виде файла типа ICS по его Telegram ID"
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
        description = "Позволяет получить доступные для выбора курсы для регистрации или изменения данных"
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
        summary = "Получение доступных для выбора программ",
        description = "Позволяет получить доступные для выбора программ для регистрации или изменения данных",
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
        description = "Позволяет получить доступные для выбора группы для регистрации или изменения данных",
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
        description = "Позволяет получить доступные для выбора подгруппы для регистрации или изменения данных"
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