package com.melowetty.hsepermhelper.domain.model.lesson

import java.time.DayOfWeek

data class CycleTime(
    override val dayOfWeek: DayOfWeek,
    override val startTime: String,
    override val endTime: String,
) : LessonTime(dayOfWeek, startTime, endTime) {
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