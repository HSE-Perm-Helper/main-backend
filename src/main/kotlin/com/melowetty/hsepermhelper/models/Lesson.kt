package com.melowetty.hsepermhelper.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

data class Lesson(
    @Schema(description = "Учебный предмет", example = "Программирование")
    val subject: String,
    @JsonIgnore val course: Int,
    @JsonIgnore val programme: String,
    @JsonIgnore val group: String,
    @JsonIgnore val subGroup: Int?,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата пары", example = "03.09.2023", type = "string")
    val date: LocalDate,
    @Schema(description = "Время начала пары", example = "8:10")
    @JsonProperty("startTime")
    val startTimeStr: String,
    @Schema(description = "Время окончания пары", example = "9:30")
    @JsonProperty("endTime")
    val endTimeStr: String,
    @JsonIgnore val startTime: LocalDateTime,
    @JsonIgnore val endTime: LocalDateTime,
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
) : Comparable<Lesson> {
    /**
     * Returns lesson will be in online mode
     *
     * @return true if lesson is online else false
     */
    fun isOnline(): Boolean {
        if(places == null) return false
        if(links?.isNotEmpty() == true) return true
        return (places.all { it.building == null } || places.all { it.building == 0 }) && lessonType != LessonType.ENGLISH
    }

    override fun compareTo(other: Lesson): Int {
        return date.compareTo(other.date)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Lesson

        if (subject != other.subject) return false
        if (course != other.course) return false
        if (programme != other.programme) return false
        if (group != other.group) return false
        if (subGroup != other.subGroup) return false
        if (date != other.date) return false
        if (startTimeStr != other.startTimeStr) return false
        if (endTimeStr != other.endTimeStr) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (lecturer != other.lecturer) return false
        if (places != other.places) return false
        if (links != other.links) return false
        if (additionalInfo != other.additionalInfo) return false
        if (lessonType != other.lessonType) return false
        if (parentScheduleType != other.parentScheduleType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subject.hashCode()
        result = 31 * result + course
        result = 31 * result + programme.hashCode()
        result = 31 * result + group.hashCode()
        result = 31 * result + (subGroup ?: 0)
        result = 31 * result + date.hashCode()
        result = 31 * result + startTimeStr.hashCode()
        result = 31 * result + endTimeStr.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + (lecturer?.hashCode() ?: 0)
        result = 31 * result + (places?.hashCode() ?: 0)
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (additionalInfo?.hashCode() ?: 0)
        result = 31 * result + lessonType.hashCode()
        result = 31 * result + parentScheduleType.hashCode()
        return result
    }
}
