package com.melowetty.hsepermhelper.controller.request

data class ApiUserUpdateRequest(
    val group: String? = null,
    val isEnabledNewScheduleNotifications: Boolean? = null,
    val isEnabledChangedScheduleNotifications: Boolean? = null,
    val isEnabledComingLessonsNotifications: Boolean? = null,
)
