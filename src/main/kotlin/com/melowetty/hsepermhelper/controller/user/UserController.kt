package com.melowetty.hsepermhelper.controller.user

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.Response
import com.melowetty.hsepermhelper.service.OldUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Пользователи", description = "Взаимодействие с пользователями")
@RestController
@RequestMapping
@Deprecated("Use /api/v3/users")
class UserController(
    private val oldUserService: OldUserService,
) {
    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение всех пользователей",
        description = "Позволяет получить всех пользователей"
    )
    @GetMapping(
        "users",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getUsers(): Response<List<UserDto>> {
        val users = oldUserService.getAllUsers()
        return Response(users)
    }
}