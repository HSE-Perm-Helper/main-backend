package com.melowetty.hsepermhelper.model

import java.time.DayOfWeek

data class CycleTime(
    val dayOfWeek: DayOfWeek,
    override val startTime: String,
    override val endTime: String,
) : LessonTime(startTime, endTime) {
    override fun compareTo(other: LessonTime): Int {
        if (other is CycleTime) {
            return compareBy(CycleTime::dayOfWeek, CycleTime::startLocaltime, CycleTime::endLocaltime).compare(
                this,
                other
            )
        }
        return compareBy(LessonTime::startLocaltime, LessonTime::endLocaltime).compare(this, other)
    }
}