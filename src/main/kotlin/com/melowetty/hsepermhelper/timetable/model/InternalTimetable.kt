package com.melowetty.hsepermhelper.timetable.model

import java.time.LocalDate
import java.time.LocalDateTime

open class InternalTimetable(
    var id: String? = null,
    val number: Int?,
    open val lessons: List<InternalLesson>,
    val start: LocalDate,
    val end: LocalDate,
    val type: InternalTimetableType,
    val educationType: EducationType,
    val isParent: Boolean,
    val source: InternalTimetableSource,
    val created: LocalDateTime = LocalDateTime.now(),
    val updated: LocalDateTime = LocalDateTime.now(),
) {
    val lessonsHash  by lazy { lessons.hashCode() }

    fun id() = id ?: throw IllegalStateException("id is null")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InternalTimetable) return false

        if (id != other.id) return false
        if (number != other.number) return false
        if (lessons != other.lessons) return false
        if (start != other.start) return false
        if (end != other.end) return false
        if (type != other.type) return false
        if (isParent != other.isParent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (number ?: 0)
        result = 31 * result + lessons.hashCode()
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + isParent.hashCode()
        return result
    }

    fun copy(
        id: String? = this.id,
        number: Int? = this.number,
        lessons: List<InternalLesson> = this.lessons,
        start: LocalDate = this.start,
        end: LocalDate = this.end,
        type: InternalTimetableType = this.type,
        educationType: EducationType = this.educationType,
        isParent: Boolean = this.isParent,
        source: InternalTimetableSource = this.source,
        created: LocalDateTime = this.created,
        updated: LocalDateTime = this.updated,
    ): InternalTimetable {
        return InternalTimetable(id, number, lessons, start, end, type, educationType, isParent, source, created, updated)
    }
}
