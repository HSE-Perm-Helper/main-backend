package com.melowetty.hsepermhelper.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

data class LessonV2(
    @Schema(description = "Учебный предмет", example = "Программирование")
    val subject: String,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата пары", example = "03.09.2023", type = "string")
    val date: LocalDate,
    @Schema(description = "Время начала пары", example = "8:10")
    @JsonProperty("startTime")
    val startTimeStr: String,
    @Schema(description = "Время окончания пары", example = "9:30")
    @JsonProperty("endTime")
    val endTimeStr: String,
    @Schema(description = "Преподаватель", example = "Викентьева О.Л.", nullable = true)
    val lecturer: String?,
    @Schema(description = "Место проведения", nullable = true)
    val places: List<LessonPlace>? = null,
    @Schema(description = "Ссылки на пару (null - если ссылок нет)")
    val links: List<String>? = null,
    @Schema(description = "Дополнительная информация о паре (null - если информации нет)")
    val additionalInfo: List<String>? = null,
    @Schema(description = "Тип лекции", example = "SEMINAR")
    val lessonType: LessonType,
    @Schema(description = "Тип расписания-родителя", example = "COMMON_WEEK_SCHEDULE")
    val parentScheduleType: ScheduleType,
    val isOnline: Boolean,
)
