package com.melowetty.hsepermhelper.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.utils.DateUtils
import java.time.LocalDate

data class ScheduledTime(
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    val date: LocalDate,
    override val startTime: String,
    override val endTime: String,
): LessonTime(startTime, endTime) {
    override fun compareTo(other: LessonTime): Int {
        if(other is ScheduledTime) {
            return compareBy(ScheduledTime::date, ScheduledTime::startLocaltime, ScheduledTime::endLocaltime).compare(this, other)
        }
        return compareBy(LessonTime::startTime, LessonTime::endTime).compare(this, other)
    }
}