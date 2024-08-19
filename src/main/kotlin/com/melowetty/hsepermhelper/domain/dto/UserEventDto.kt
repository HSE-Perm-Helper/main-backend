package com.melowetty.hsepermhelper.domain.dto

import com.melowetty.hsepermhelper.model.UserEventType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class UserEventDto(
    @Schema(description = "ID ивента")
    val id: Long? = null,

    @Schema(description = "Время и дата выполнения ивента")
    val date: LocalDateTime = LocalDateTime.now(),

    @Schema(description = "Какой пользователь вызвал ивент")
    val targetUser: UserDto,

    @Schema(description = "Тип ивента")
    val userEventType: UserEventType,
)