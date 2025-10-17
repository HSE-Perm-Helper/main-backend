package com.melowetty.hsepermhelper.controller.user

import com.melowetty.hsepermhelper.domain.model.user.UserCreateRequest
import com.melowetty.hsepermhelper.controller.request.UserSetEmailRequest
import com.melowetty.hsepermhelper.domain.dto.EmailVerificationDto
import com.melowetty.hsepermhelper.domain.dto.HideLessonDto
import com.melowetty.hsepermhelper.domain.dto.RemoteScheduleLink
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.Response
import com.melowetty.hsepermhelper.service.UserService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v2/users")
@Validated
class UserV2Controller(
    private val userService: UserService
) {
    @GetMapping("{id}")
    fun getUserByTelegramId(
        @PathVariable("id")
        id: Long,
    ): Response<UserDto> {
        return Response(userService.getByTelegramId(id))
    }

    @DeleteMapping("{id}")
    fun deleteUserByTelegramId(
        @PathVariable("id")
        telegramId: Long,
    ): Response<String> {
        userService.deleteByTelegramId(telegramId)
        return Response("Пользователь успешно удалён!")
    }

    @PatchMapping("{id}")
    fun updateUserByTelegramId(
        @PathVariable("id")
        telegramId: Long,
        @RequestBody
        @Parameter(description = "Новые настройки пользователя")
        settings: Map<String, Any>,
    ): Response<UserDto> {
        val user = userService.updateUserSettings(telegramId, settings)
        return Response(user)
    }

    @GetMapping
    fun getUsers(): Response<List<UserDto>> {
        val users = userService.getAllUsers()
        return Response(users)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createUser(
        @RequestBody request: UserCreateRequest,
    ): Response<UserDto> {
        val user = userService.create(request)
        return Response(user)
    }

    @PutMapping
    fun updateUser(
        @RequestBody userDto: UserDto,
    ): Response<UserDto> {
        val user = userService.updateUser(userDto)
        return Response(user)
    }

    @PostMapping("{id}/hidden-lessons")
    fun addHiddenLesson(@PathVariable("id") telegramId: Long, @RequestBody lesson: HideLessonDto): UserDto {
        return userService.addHiddenLesson(telegramId, lesson)
    }

    @DeleteMapping("{id}/hidden-lessons")
    fun removeHiddenLesson(@PathVariable("id") telegramId: Long, @RequestBody lesson: HideLessonDto): UserDto {
        return userService.removeHiddenLesson(telegramId, lesson)
    }

    @GetMapping("{id}/remote-schedule")
    fun getRemoteScheduleLink(@PathVariable("id") telegramId: Long): RemoteScheduleLink {
        return userService.getRemoteScheduleLink(telegramId)
    }

    @PostMapping("{id}/remote-schedule")
    fun createOrUpdateScheduleLink(@PathVariable("id") telegramId: Long): RemoteScheduleLink {
        return userService.createOrUpdateScheduleLink(telegramId)
    }

    @PostMapping("{id}/email")
    fun setOrUpdateEmailRequest(@PathVariable("id") id: Long, @RequestBody request: UserSetEmailRequest
    ): EmailVerificationDto {
        return userService.setOrUpdateEmailRequest(id, request.email)
    }

    @DeleteMapping("{id}/email")
    fun setOrUpdateEmailRequest(@PathVariable("id") id: Long) {
        return userService.deleteEmail(id)
    }
}