package com.melowetty.hsepermhelper.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Расписание занятий")
data class ScheduleV3(
    @Schema(description = "Номер недели", example = "6", nullable = true)
    val weekNumber: Int?,
    val lessons: List<Lesson>,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата начала недели", example = "03.09.2023", type = "string")
    val weekStart: LocalDate,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата конца недели", example = "10.09.2023", type = "string")
    val weekEnd: LocalDate,
    @Schema(description = "Тип расписания")
    val scheduleType: ScheduleType,
) {
    override fun hashCode(): Int {
        var result = weekNumber ?: 0
        result = 31 * result + lessons.hashCode()
        result = 31 * result + weekStart.hashCode()
        result = 31 * result + weekEnd.hashCode()
        result = 31 * result + scheduleType.hashCode()
        return result
    }

    companion object {
        fun Schedule.toScheduleInfo(): ScheduleInfo {
            return ScheduleInfo(
                weekNumber = weekNumber,
                weekStart = weekStart,
                weekEnd = weekEnd,
                scheduleType = scheduleType,
                hashcode = hashCode()
            )
        }
    }
}

