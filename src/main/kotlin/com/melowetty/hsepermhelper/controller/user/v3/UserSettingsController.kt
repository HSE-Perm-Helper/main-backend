package com.melowetty.hsepermhelper.controller.user.v3

import com.melowetty.hsepermhelper.domain.dto.ApiUserHideLesson
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.service.PersonalTimetableService
import com.melowetty.hsepermhelper.service.UserHiddenLessonService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/v3/users/{id}/settings")
class UserSettingsController(
    private val hiddenLessonService: UserHiddenLessonService,
) {
    @GetMapping("hidden-lessons")
    fun getHiddenLessons(
        @PathVariable("id") userId: UUID,
    ): List<AvailableLessonForHiding> {
        return hiddenLessonService.getLessonsForHiding(userId)
    }

    @PostMapping("hidden-lessons")
    fun addHiddenLesson(
        @PathVariable("id") userId: UUID,
        @RequestBody lesson: ApiUserHideLesson
    ) {
        hiddenLessonService.hideLesson(userId, lesson.lesson, lesson.lessonType, lesson.subGroup)
    }

    @DeleteMapping("hidden-lessons")
    fun removeHiddenLesson(
        @PathVariable("id") userId: UUID,
        @RequestBody lesson: ApiUserHideLesson
    ) {
        hiddenLessonService.unHideLesson(userId, lesson.lesson, lesson.lessonType, lesson.subGroup)
    }
}