package com.melowetty.hsepermhelper.domain.model.user

import com.melowetty.hsepermhelper.domain.model.Field
import com.melowetty.hsepermhelper.timetable.model.EducationType

data class UserChangeRequest(
    val group: Field<String> = Field.Unset,
    val educationType: Field<EducationType> = Field.Unset,
    val email: Field<String?> = Field.Unset,
    val isEnabledNewScheduleNotifications: Field<Boolean> = Field.Unset,
    val isEnabledChangedScheduleNotifications: Field<Boolean> = Field.Unset,
    val isEnabledComingLessonsNotifications: Field<Boolean> = Field.Unset,
)
