package com.melowetty.hsepermhelper.controllers

import com.melowetty.hsepermhelper.models.*
import com.melowetty.hsepermhelper.service.ScheduleService
import com.melowetty.hsepermhelper.service.UserEventService
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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
    fun getScheduleV2(
        @Parameter(description = "Telegram ID пользователя")
        @PathVariable("telegramId")
        telegramId: Long,
    ): Response<List<ScheduleV2>> {
        userEventService.addUserEvent(telegramId, UserEventType.GET_SCHEDULE)
        val schedules = scheduleService.getUserSchedulesByTelegramId(telegramId).filter { it.scheduleType != ScheduleType.QUARTER_SCHEDULE }.map { it.toScheduleV2() }
        return Response(schedules)
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение всех доступных расписаний",
        description = "Позволяет получить данные о расписаниях"
    )
    @GetMapping(
        "v3/schedules",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getSchedules(
    ): Response<List<ScheduleInfo>> {
        val schedules = scheduleService.getAvailableSchedules()
        return Response(schedules)
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение расписания пользователя",
        description = "Позволяет получить расписания пользователя по его Telegram ID"
    )
    @GetMapping(
        "v3/schedule/{telegramId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getScheduleV3(
        @Parameter(description = "Telegram ID пользователя")
        @PathVariable("telegramId")
        telegramId: Long,
        @RequestParam("start") start: String,
        @RequestParam("end") end: String
    ): Response<Schedule> {
        val startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern(DateUtils.DATE_PATTERN))
        val endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern(DateUtils.DATE_PATTERN))
        userEventService.addUserEvent(telegramId, UserEventType.GET_SCHEDULE)
        val schedule = scheduleService.getUserScheduleByTelegramId(telegramId, startDate, endDate)
        return Response(schedule)
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение расписания пользователя",
        description = "Позволяет получить расписания пользователя по его Telegram ID"
    )
    @GetMapping(
        "v3/schedules/{telegramId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getUserSchedulesV3(
        @Parameter(description = "Telegram ID пользователя")
        @PathVariable("telegramId")
        telegramId: Long,
    ): Response<List<Schedule>> {
        userEventService.addUserEvent(telegramId, UserEventType.GET_SCHEDULE)
        val schedule = scheduleService.getUserSchedulesByTelegramId(telegramId)
        return Response(schedule)
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