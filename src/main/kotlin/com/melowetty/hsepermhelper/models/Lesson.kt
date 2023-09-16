package com.melowetty.hsepermhelper.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.melowetty.hsepermhelper.utils.DateUtils
import com.melowetty.hsepermhelper.utils.EmojiCode
import io.swagger.v3.oas.annotations.media.Schema
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Description
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
    @Schema(description = "Кабинет", example = "121", nullable = true)
    val office: String?,
    @Schema(description = "Корпус (если 0 - пара дистанционная)", example = "2", nullable = true)
    val building: Int?,
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
        if(building == null && office == null) return false
        return (building == null || building == 0) && lessonType != LessonType.ENGLISH
    }

    /**
     * Converts lesson object to VEvent for import in calendar
     *
     * @return converted lesson to VEvent object
     */
    fun toVEvent(): VEvent {
        val quarterScheduleSymbol =
            if(parentScheduleType == ScheduleType.QUARTER_SCHEDULE) "*" else ""
        val distantSymbol = if(isOnline()) EmojiCode.DISTANT_LESSON_SYMBOL else ""
        val event = VEvent(startTime, endTime,
            "${distantSymbol}${lessonType.toEventSubject(subject)}${quarterScheduleSymbol}")
        val descriptionLines: MutableList<String> = mutableListOf()
        if (lecturer != null) {
            descriptionLines.add("Преподаватель: $lecturer")
        }
        if(isOnline()) {
            if (links != null) {
                descriptionLines.add("Ссылка на пару: ${links[0]}")
                if (links.size > 1) {
                    descriptionLines.add("Дополнительные ссылки на пару: ")
                    links.subList(1, links.size).forEach { descriptionLines.add(it) }
                }
            }
            else {
                descriptionLines.add("Место: онлайн")
            }
        } else {
            if (building == null && office == null) {
                if(lessonType == LessonType.COMMON_MINOR) {
                    descriptionLines.add("Информацию о времени и ссылке на майнор узнайте " +
                            "подробнее в HSE App X или в системе РУЗ")
                }
                else {
                    descriptionLines.add("Место: не указано")
                }
            }
            else {
                descriptionLines.add("Место: $building корпус - ${getOfficeStr()}")
            }
        }
        if(parentScheduleType == ScheduleType.QUARTER_SCHEDULE) {
            descriptionLines.add("\n" +
                    "* - пара взята из расписания на модуль, фактическое расписание " +
                    "может отличаться от этого")
        }
        event.add(
            Description(
                descriptionLines.joinToString("\n")
            )
        )
        return event
    }

    private fun getOfficeStr(): String? {
        if(office == null) return null
        return if(office.toIntOrNull() == null) {
            if (office.contains(",")) return "кабинеты $office"
            office
        }
        else "кабинет $office"
    }

    override fun compareTo(other: Lesson): Int {
        return date.compareTo(other.date)
    }
}
