package com.melowetty.hsepermhelper.timetable.model

enum class InternalTimetableType(
    val limitForSettings: Int? = null
) {
    BACHELOR_WEEK_SCHEDULE(2),
    BACHELOR_SESSION_SCHEDULE(0),
    BACHELOR_QUARTER_SCHEDULE
}