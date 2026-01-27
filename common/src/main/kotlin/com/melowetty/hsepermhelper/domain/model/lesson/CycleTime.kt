package com.melowetty.hsepermhelper.domain.model.lesson

import java.time.DayOfWeek

data class CycleTime(
    override val dayOfWeek: DayOfWeek,
    override val startTime: String,
    override val endTime: String,
) : LessonTime(dayOfWeek, startTime, endTime)