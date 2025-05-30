package com.melowetty.hsepermhelper.domain.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.domain.model.UserRole
import com.melowetty.hsepermhelper.util.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.UUID

@Schema(name = "User")
data class UserDto(
    @Schema(description = "ID пользователя", example = "UUID")
    val id: UUID = UUID.randomUUID(),
    @Schema(description = "Telegram ID пользователя", example = "123432412")
    val telegramId: Long = 0L,

    val email: String? = null,
    @Schema(description = "Настройки пользователя")
    val settings: SettingsDto,

    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    val createdDate: LocalDateTime = LocalDateTime.now(),

    val roles: List<UserRole>,
)