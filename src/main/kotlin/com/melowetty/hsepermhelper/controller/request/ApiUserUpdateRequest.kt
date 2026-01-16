package com.melowetty.hsepermhelper.controller.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class ApiUserUpdateRequest(
    val group: String? = null,

    @Schema(description = "Включены ли уведомления о новом расписании", example = "true")
    @JsonProperty("isEnabledNewScheduleNotifications")
    val isEnabledNewScheduleNotifications: Boolean? = null,

    @Schema(description = "Включены ли уведомления о изменении расписания", example = "true")
    @JsonProperty("isEnabledChangedScheduleNotification")
    val isEnabledChangedScheduleNotifications: Boolean? = null,

    @JsonProperty("isEnabledComingLessonsNotifications")
    val isEnabledComingLessonsNotifications: Boolean? = null,
)
