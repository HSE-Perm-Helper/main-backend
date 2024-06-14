package com.melowetty.hsepermhelper.model

data class SchedulesChanging(
    val added: List<Schedule> = listOf(),
    val changed: List<ScheduleDifference> = listOf(),
    val deleted: List<Schedule> = listOf(),
)