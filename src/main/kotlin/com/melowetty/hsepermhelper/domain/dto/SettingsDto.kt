package com.melowetty.hsepermhelper.domain.dto

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

    val hiddenLessons: Set<HideLessonDto>,

    @Schema(description = "Включены ли уведомления о новом расписании", example = "true")
    val isEnabledNewScheduleNotifications: Boolean = true,

    @Schema(description = "Включены ли уведомления о изменении расписания", example = "true")
    val isEnabledChangedScheduleNotifications: Boolean = true,

    val isEnabledComingLessonsNotifications: Boolean = false,
)