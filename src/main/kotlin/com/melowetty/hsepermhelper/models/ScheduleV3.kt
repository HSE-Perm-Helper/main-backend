package com.melowetty.hsepermhelper.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Расписание занятий")
data class ScheduleV3(
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
) {
    override fun hashCode(): Int {
        var result = number ?: 0
        result = 31 * result + lessons.hashCode()
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + scheduleType.hashCode()
        return result
    }
}

