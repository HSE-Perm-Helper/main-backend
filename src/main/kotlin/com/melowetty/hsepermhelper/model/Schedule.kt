package com.melowetty.hsepermhelper.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.util.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Расписание занятий")
data class Schedule(
    @Schema(description = "Номер расписания", example = "6", nullable = true)
    val number: Int?,
    val lessons: List<Lesson>,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата начала расписания", example = "03.09.2023", type = "string")
    val start: LocalDate,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата конца расписания", example = "10.09.2023", type = "string")
    val end: LocalDate,
    @Schema(description = "Тип расписания")
    val scheduleType: ScheduleType,
)

