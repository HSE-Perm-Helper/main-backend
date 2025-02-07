package com.melowetty.hsepermhelper.domain.model.lesson

import com.melowetty.hsepermhelper.model.schedule.ScheduleType
import io.swagger.v3.oas.annotations.media.Schema

data class Lesson(
    @Schema(description = "Учебный предмет", example = "Программирование")
    val subject: String,
    val subGroup: Int?,
    val time: com.melowetty.hsepermhelper.domain.model.lesson.LessonTime,
    @Schema(description = "Преподаватель", example = "Викентьева О.Л.", nullable = true)
    val lecturer: String?,
    @Schema(description = "Место проведения", nullable = true)
    val places: List<com.melowetty.hsepermhelper.domain.model.lesson.LessonPlace>? = null,
    @Schema(description = "Ссылки на пару (null - если ссылок нет)")
    val links: List<String>? = null,
    @Schema(description = "Дополнительная информация о паре (null - если информации нет)")
    val additionalInfo: List<String>? = null,
    @Schema(description = "Тип лекции", example = "SEMINAR")
    val lessonType: com.melowetty.hsepermhelper.domain.model.lesson.LessonType,
    @Schema(description = "Тип расписания-родителя", example = "WEEK_SCHEDULE")
    val parentScheduleType: ScheduleType,
) : Comparable<com.melowetty.hsepermhelper.domain.model.lesson.Lesson> {
    /**
     * Returns lesson will be in online mode
     *
     * @return true if lesson is online else false
     */
    fun isOnline(): Boolean {
        if (links?.isNotEmpty() == true) return true
        if (places == null) return false
        return (places.all { it.building == null } || places.all { it.building == 0 }) && lessonType != com.melowetty.hsepermhelper.domain.model.lesson.LessonType.ENGLISH
    }

    override fun compareTo(other: com.melowetty.hsepermhelper.domain.model.lesson.Lesson): Int {
        return time.compareTo(other.time)
    }
}
