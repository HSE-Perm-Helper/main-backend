package com.melowetty.hsepermhelper.model.event

import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.schedule.ScheduleDifference

data class ExcelSchedulesChanging(
    val added: List<Schedule> = listOf(),
    val changed: List<ScheduleDifference> = listOf(),
    val deleted: List<Schedule> = listOf(),
)