package com.melowetty.hsepermhelper.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Информация о расписании")
data class ScheduleInfo (
    @Schema(description = "Номер недели", example = "6", nullable = true)
    val weekNumber: Int?,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата начала недели", example = "03.09.2023", type = "string")
    val weekStart: LocalDate,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата конца недели", example = "10.09.2023", type = "string")
    val weekEnd: LocalDate,
    @Schema(description = "Тип расписания")
    val scheduleType: ScheduleType,
    @Schema(description = "Хэшкод расписания")
    val hashcode: Int,
) {
    fun toSchedule(): Schedule {
        return Schedule(
            weekNumber = weekNumber,
            weekStart = weekStart,
            weekEnd = weekEnd,
            scheduleType = scheduleType,
            lessons = listOf()
        )
    }
}