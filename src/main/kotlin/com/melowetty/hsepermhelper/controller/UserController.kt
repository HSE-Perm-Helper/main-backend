package com.melowetty.hsepermhelper.controller

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.model.Response
import com.melowetty.hsepermhelper.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "Пользователи", description = "Взаимодействие с пользователями")
@RestController
@RequestMapping()
class UserController(
    private val userService: UserService,
) {
    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение пользователя по Telegram ID",
        description = "Позволяет получить пользователя по его Telegram ID"
    )
    @GetMapping(
        "user",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getUserByTelegramId(
        @Parameter(description = "Telegram ID пользователя")
        @RequestParam("telegramId")
        telegramId: Long,
    ): Response<UserDto> {
        return Response(userService.getByTelegramId(telegramId = telegramId))
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение пользователя",
        description = "Позволяет получить пользователя по его ID"
    )
    @GetMapping(
        "user/{id}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getUserById(
        @Parameter(description = "ID пользователя")
        @PathVariable("id")
        id: UUID,
    ): Response<UserDto> {
        return Response(userService.getById(id))
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Удаление пользователя",
        description = "Позволяет удалить пользователя по его ID"
    )
    @DeleteMapping(
        "user/{id}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteUserById(
        @Parameter(description = "ID пользователя")
        @PathVariable("id")
        id: UUID,
    ): Response<String> {
        userService.deleteById(id)
        return Response("Пользователь успешно удалён!")
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Удаление пользователя",
        description = "Позволяет удалить пользователя по его Telegram ID"
    )
    @DeleteMapping(
        "user",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteUserByTelegramId(
        @Parameter(description = "Telegram ID пользователя")
        @RequestParam("telegramId")
        telegramId: Long,
    ): Response<String> {
        userService.deleteByTelegramId(telegramId)
        return Response("Пользователь успешно удалён!")
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Изменение настроек пользователя",
        description = "Позволяет изменить настройки пользователя по Telegram ID"
    )
    @PatchMapping(
        "user",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateUserByTelegramId(
        @Parameter(description = "Telegram ID пользователя")
        @RequestParam("telegramId")
        telegramId: Long,
        @RequestBody
        @Parameter(description = "Новые настройки пользователя")
        settings: Map<String, Any>,
    ): Response<UserDto> {
        val user = userService.updateUserSettings(telegramId, settings)
        return Response(user)
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Получение всех пользователей",
        description = "Позволяет получить всех пользователей"
    )
    @GetMapping(
        "users",
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUsers(): Response<List<UserDto>> {
        val users = userService.getAllUsers()
        return Response(users)
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Регистрация пользователя",
        description = "Позволяет зарегистрировать пользователя"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
        "users",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(
        @RequestBody userDto: UserDto,
    ): Response<UserDto> {
        val user = userService.create(dto = userDto)
        return Response(user)
    }

    @SecurityRequirement(name = "X-Secret-Key")
    @Operation(
        summary = "Обновление пользователя",
        description = "Позволяет обновить данные пользователя"
    )
    @PutMapping(
        "users",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUser(
        @RequestBody userDto: UserDto,
    ): Response<UserDto> {
        val user = userService.updateUser(userDto)
        return Response(user)
    }
}