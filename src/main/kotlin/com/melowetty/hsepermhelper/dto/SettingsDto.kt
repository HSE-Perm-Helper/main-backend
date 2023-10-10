package com.melowetty.hsepermhelper.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Настройки пользователя")
data class SettingsDto(
    @JsonIgnore
    val id: Long? = null,

    @Schema(description = "Учебная группа пользователя", example = "РИС-22-3")
    val group: String = "",

    @Schema(description = "Учебная подгруппа пользователя", example = "5")
    val subGroup: Int = 0,

    @Schema(description = "Включать ли расписание на модуль в общее расписание", example = "false")
    val includeQuarterSchedule: Boolean = false,

    @Schema(description = "Включать ли общие пары английского в общее расписание", example = "false")
    val includeCommonEnglish: Boolean = false,

    @Schema(description = "Включать ли общие пары майнора в общее расписание", example = "true")
    val includeCommonMinor: Boolean = true,

    @JsonIgnore
    @Schema(description = "Включен ли удаленный календарь", example = "false")
    val isEnabledRemoteCalendar: Boolean = false,

    @Schema(description = "Включены ли уведомления о новом расписании", example = "true")
    val isEnabledNewCommonScheduleNotifications: Boolean = true,

    @Schema(description = "Включены ли уведомления о новом расписании на модуль", example = "false")
    val isEnabledNewQuarterScheduleNotifications: Boolean = false,
)