package com.melowetty.hsepermhelper.domain.model.user

import com.melowetty.hsepermhelper.domain.model.timetable.EducationType

data class UserChangeRequest(
    val group: String? = null,
    val educationType: EducationType? = null,
    val email: String? = null,
    val emailPresent: Boolean = false,
    val isEnabledNewScheduleNotifications: Boolean? = null,
    val isEnabledChangedScheduleNotifications: Boolean? = null,
    val isEnabledComingLessonsNotifications: Boolean? = null,
)
