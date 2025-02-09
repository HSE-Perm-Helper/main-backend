package com.melowetty.hsepermhelper.domain.model.schedule

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.util.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Информация о расписании")
data class ScheduleInfo(
    val number: Int?,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    val start: LocalDate,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    val end: LocalDate,
    val scheduleType: ScheduleType,
)