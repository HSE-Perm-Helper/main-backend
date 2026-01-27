package com.melowetty.hsepermhelper.domain.model.lesson

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.util.DateUtils
import java.time.LocalDate

data class ScheduledTime(
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    val date: LocalDate,
    override val startTime: String,
    override val endTime: String,
) : LessonTime(date.dayOfWeek, startTime, endTime)