package com.melowetty.hsepermhelper.controller.user.v3

import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.service.PersonalTimetableService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/v3/users/{id}/timetables")
class UserTimetableController(
    private val personalTimetableService: PersonalTimetableService
) {
    @GetMapping
    fun getTimetables(@PathVariable("id") userId: UUID): List<ScheduleInfo> {
        return personalTimetableService.getTimetables(userId)
    }

    @GetMapping("/today")
    fun getTodayLessons(
        @PathVariable("id") userId: UUID,
    ): List<Lesson> {
        return personalTimetableService.getTodayLessons(userId)
    }

    @GetMapping("/tomorrow")
    fun getTomorrowLessons(
        @PathVariable("id") userId: UUID,
    ): List<Lesson> {
        return personalTimetableService.getTomorrowLessons(userId)
    }

    @GetMapping("/{timetableId}")
    fun getTimetable(
        @PathVariable("id") userId: UUID,
        @PathVariable("timetableId") timetableId: String,
    ): Schedule {
        return personalTimetableService.getTimetable(userId, timetableId)
    }
}