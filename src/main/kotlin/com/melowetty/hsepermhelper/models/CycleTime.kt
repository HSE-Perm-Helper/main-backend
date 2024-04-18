package com.melowetty.hsepermhelper.models

import java.time.DayOfWeek

data class CycleTime(
    val dayOfWeek: DayOfWeek,
    override val startTime: String,
    override val endTime: String,
    ): LessonTime(startTime, endTime) {
    override fun compareTo(other: LessonTime): Int {
        if(other is CycleTime) {
            return compareBy(CycleTime::dayOfWeek, CycleTime::startTime, CycleTime::endTime).compare(this, other)
        }
        return compareBy(LessonTime::startTime, LessonTime::endTime).compare(this, other)
    }
}