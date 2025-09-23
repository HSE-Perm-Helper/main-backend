package com.melowetty.hsepermhelper.controller

import com.melowetty.hsepermhelper.domain.model.Response
import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.domain.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.service.PersonalScheduleService
import com.melowetty.hsepermhelper.service.ScheduleInfoService
import com.melowetty.hsepermhelper.service.UserEventService
import com.melowetty.hsepermhelper.util.DateUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@Tag(name = "Расписание", description = "Взаимодействие с расписанием")
@RestController
class ScheduleController(
    private val personalScheduleService: PersonalScheduleService,
    private val userEventService: UserEventService,
    private val scheduleInfoService: ScheduleInfoService,
) {
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
        val schedules = personalScheduleService.getAvailableSchedules()
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
        val schedule = personalScheduleService.getUserScheduleByTelegramId(telegramId, startDate, endDate)
        return Response(schedule)
    }

    @GetMapping(
        "v3/schedule/{telegramId}/today",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getTodaySchedule(
        @PathVariable telegramId: Long,
    ): Response<List<Lesson>> {
        val schedule = personalScheduleService.getTodayLessons(telegramId)
        userEventService.addUserEvent(telegramId, UserEventType.GET_TODAY_SCHEDULE)
        return Response(schedule)
    }

    @GetMapping(
        "v3/schedule/{telegramId}/tomorrow",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getTomorrowSchedule(
        @PathVariable telegramId: Long,
    ): Response<List<Lesson>> {
        val schedule = personalScheduleService.getTomorrowLessons(telegramId)
        userEventService.addUserEvent(telegramId, UserEventType.GET_TOMORROW_SCHEDULE)
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
        val schedule = personalScheduleService.getUserSchedulesByTelegramId(telegramId)
        return Response(schedule)
    }

    @Deprecated("Use v1/schedule-info/courses")
    @Operation(deprecated = true)
    @GetMapping(
        "schedule/available_courses",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailableCourses(
    ): Response<List<Int>> {
        return Response(scheduleInfoService.getAvailableCourses())
    }

    @Deprecated("Use v1/schedule-info/programs")
    @Operation(deprecated = true)
    @GetMapping(
        "schedule/available_programs",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailablePrograms(
        @Parameter(description = "Номер курса")
        @RequestParam("course")
        course: Int,
    ): Response<List<String>> {
        return Response(scheduleInfoService.getAvailablePrograms(course))
    }

    @Deprecated("Use v1/schedule-info/groups")
    @Operation(deprecated = true)
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
        return Response(scheduleInfoService.getAvailableGroups(course, program))
    }

    @GetMapping(
        "schedule/lessons-for-hiding",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAvailableLessonsForHiding(@RequestParam telegramId: Long): List<AvailableLessonForHiding> {
        return personalScheduleService.getAvailableLessonsForHiding(telegramId).sortedBy { it.lessonType.ordinal }
            .sortedBy { it.lesson }
    }
}