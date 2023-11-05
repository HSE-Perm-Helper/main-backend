package com.melowetty.hsepermhelper.models.v2

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonGetter
import com.melowetty.hsepermhelper.models.ScheduleInfo
import com.melowetty.hsepermhelper.models.ScheduleType
import com.melowetty.hsepermhelper.models.v1.ScheduleV1
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Schema(description = "Расписание занятий")
data class ScheduleV2(
    @Schema(description = "Номер недели", example = "6", nullable = true)
    val weekNumber: Int?,
    val lessons: List<LessonV2>,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата начала недели", example = "03.09.2023", type = "string")
    val weekStart: LocalDate,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата конца недели", example = "10.09.2023", type = "string")
    val weekEnd: LocalDate,
    @Schema(description = "Тип расписания")
    val scheduleType: ScheduleType,
) {
    @JsonGetter("lessons")
    fun getFormattedLessons(): Set<Map.Entry<String, List<LessonV2>>> {
        return lessons.sortedBy { it.date }.groupBy { it.date }.mapKeys { it.key.format(DateTimeFormatter.ofPattern(DateUtils.DATE_PATTERN)) }.entries
    }

    override fun hashCode(): Int {
        var result = weekNumber ?: 0
        result = 31 * result + lessons.hashCode()
        result = 31 * result + weekStart.hashCode()
        result = 31 * result + weekEnd.hashCode()
        result = 31 * result + scheduleType.hashCode()
        return result
    }

    public fun toV1(): ScheduleV1 {
        return ScheduleV1(
            weekNumber = weekNumber,
            lessons = lessons.map { it.toV1() },
            weekStart = weekStart,
            weekEnd = weekEnd,
            scheduleType = scheduleType,
        )
    }

    companion object {
        fun ScheduleV2.toScheduleInfo(): ScheduleInfo {
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
