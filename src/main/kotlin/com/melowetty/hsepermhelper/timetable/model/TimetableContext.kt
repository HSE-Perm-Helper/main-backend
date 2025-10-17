package com.melowetty.hsepermhelper.timetable.model

data class TimetableContext(
    val purpose: TimetablePurpose
)

enum class TimetablePurpose {
    DISPLAY,
    SETTINGS
}
