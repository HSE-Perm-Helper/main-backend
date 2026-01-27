package com.melowetty.hsepermhelper.api.controller.user.v3

import com.melowetty.hsepermhelper.domain.request.ApiUserUpdateRequest
import com.melowetty.hsepermhelper.domain.request.UserSetEmailRequest
import com.melowetty.hsepermhelper.domain.dto.ApiUserHideLesson
import com.melowetty.hsepermhelper.domain.dto.EmailVerificationDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.service.user.UserEmailService
import com.melowetty.hsepermhelper.service.user.UserHiddenLessonService
import com.melowetty.hsepermhelper.service.user.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v3/users/{id}/settings")
class UserSettingsController(
    private val userService: UserService,
    private val hiddenLessonService: UserHiddenLessonService,
    private val emailService: UserEmailService,
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

    @PostMapping("email")
    fun setOrUpdateEmailRequest(
        @PathVariable("id") userId: UUID,
        @RequestBody request: UserSetEmailRequest
    ): EmailVerificationDto {
        return emailService.setOrUpdateEmailRequest(userId, request.email)
    }

    @DeleteMapping("email")
    fun deleteEmail(@PathVariable("id") userId: UUID) {
        return emailService.deleteEmail(userId)
    }

    @PatchMapping
    fun updateUserSettings(
            @PathVariable("id") userId: UUID,
            @RequestBody request: ApiUserUpdateRequest,
    ): UserDto {
        return userService.updateUser(userId, request)
    }
}