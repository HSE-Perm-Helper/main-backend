package com.melowetty.hsepermhelper.controller.user.v3

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/v3/users")
class UserV3Controller(
    private val userService: UserService,
) {
    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") userId: UUID): UserDto {
        return userService.getById(userId)
    }
}