package com.melowetty.hsepermhelper.domain.model.timetable

enum class InternalTimetableType(
    val limitForSettings: Int? = null,
    val shouldBeMerged: Boolean = false
) {
    BACHELOR_WEEK_TIMETABLE(2),
    BACHELOR_SESSION_TIMETABLE(0, true),
    BACHELOR_QUARTER_TIMETABLE,
    BACHELOR_ENGLISH_TIMETABLE
}