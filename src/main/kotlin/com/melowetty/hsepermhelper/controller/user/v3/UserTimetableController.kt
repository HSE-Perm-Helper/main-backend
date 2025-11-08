package com.melowetty.hsepermhelper.controller.user.v3

import com.melowetty.hsepermhelper.annotation.TrackUserEvent
import com.melowetty.hsepermhelper.domain.model.event.UserEventType
import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.service.PersonalTimetableService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v3/users/{id}/timetables")
class UserTimetableController(
    private val personalTimetableService: PersonalTimetableService,
) {
    @GetMapping
    fun getTimetables(@PathVariable("id") userId: UUID): List<ScheduleInfo> {
        return personalTimetableService.getTimetables(userId)
    }

    @GetMapping("/today")
    @TrackUserEvent(UserEventType.GET_TODAY_SCHEDULE)
    fun getTodayLessons(
        @PathVariable("id") userId: UUID,
    ): List<Lesson> {
        val timetable = personalTimetableService.getTodayLessons(userId)
        return timetable
    }

    @GetMapping("/tomorrow")
    @TrackUserEvent(UserEventType.GET_TOMORROW_SCHEDULE)
    fun getTomorrowLessons(
        @PathVariable("id") userId: UUID,
    ): List<Lesson> {
        val timetable = personalTimetableService.getTomorrowLessons(userId)
        return timetable
    }

    @GetMapping("/{timetableId}")
    @TrackUserEvent(UserEventType.GET_SCHEDULE)
    fun getTimetable(
        @PathVariable("id") userId: UUID,
        @PathVariable("timetableId") timetableId: String,
    ): Schedule {
        val timetable = personalTimetableService.getTimetable(userId, timetableId)
        return timetable
    }
}