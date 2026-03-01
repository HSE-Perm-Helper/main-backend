package com.melowetty.hsepermhelper.domain.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Настройки пользователя")
data class SettingsDto(
    @JsonIgnore
    val id: Long? = null,

    @Schema(description = "Учебная группа пользователя", example = "РИС-22-3")
    val group: String = "",

    val hiddenLessons: List<ApiUserHideLesson> = listOf(),

    @Schema(description = "Включены ли уведомления о новом расписании", example = "true")
    @get:JsonProperty("isEnabledNewScheduleNotifications")
    @param:JsonProperty("isEnabledNewScheduleNotifications")
    val isEnabledNewScheduleNotifications: Boolean = true,

    @Schema(description = "Включены ли уведомления о изменении расписания", example = "true")
    @get:JsonProperty("isEnabledChangedScheduleNotifications")
    @param:JsonProperty("isEnabledChangedScheduleNotifications")
    val isEnabledChangedScheduleNotifications: Boolean = true,

    @get:JsonProperty("isEnabledComingLessonsNotifications")
    @param:JsonProperty("isEnabledComingLessonsNotifications")
    val isEnabledComingLessonsNotifications: Boolean = false,

)