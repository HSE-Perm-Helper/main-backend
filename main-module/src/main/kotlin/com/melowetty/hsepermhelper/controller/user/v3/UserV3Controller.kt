package com.melowetty.hsepermhelper.controller.user.v3

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.user.UserCreateRequest
import com.melowetty.hsepermhelper.service.user.UserService
import jakarta.validation.constraints.Size
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@Validated
@RestController
@RequestMapping("/v3/users")
class UserV3Controller(
    private val userService: UserService,
) {
    @PostMapping
    fun createUser(
        @RequestBody request: UserCreateRequest,
    ): UserDto {
        return userService.createUser(request)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") userId: UUID): UserDto {
        return userService.getUserById(userId)
    }

    @GetMapping
    fun getUsers(
        @RequestParam("ids")
        @Size(max = 100, message = "Нельзя запрашивать больше 100 пользователей за раз")
        userIds: List<UUID>
    ): List<UserDto> {
        return userService.getUsersById(userIds)
    }
}