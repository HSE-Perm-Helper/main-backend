package com.melowetty.hsepermhelper.models

import com.melowetty.hsepermhelper.models.v2.ScheduleV2

data class ChangedSchedule(
    val before: ScheduleV2?,
    val after: ScheduleV2?
)