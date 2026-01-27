package com.melowetty.hsepermhelper.domain.model.timetable

data class TimetableContext(
    val purpose: TimetablePurpose
)

enum class TimetablePurpose {
    DISPLAY,
    SETTINGS
}
