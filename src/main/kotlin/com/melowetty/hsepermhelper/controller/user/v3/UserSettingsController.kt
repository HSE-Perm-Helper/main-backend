package com.melowetty.hsepermhelper.controller.user.v3

import com.melowetty.hsepermhelper.domain.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.service.PersonalTimetableV2Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v3/users/{id}/settings")
class UserSettingsController(
    private val personalTimetableService: PersonalTimetableV2Service
) {
    @GetMapping("hidden-lessons")
    fun getHiddenLessons(
        @PathVariable("id") userId: UUID,
    ): List<AvailableLessonForHiding> {
        return personalTimetableService.getLessonsForHiding(userId)
    }
}