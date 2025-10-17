package com.melowetty.hsepermhelper.domain.model.event

import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalScheduleDifference

data class ExcelSchedulesChanging(
    val added: List<InternalTimetable> = listOf(),
    val changed: List<InternalScheduleDifference> = listOf(),
    val deleted: List<InternalTimetable> = listOf(),
)