package com.melowetty.hsepermhelper.timetable.model

import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import java.time.LocalDate
import java.time.LocalDateTime

class ExcelTimetable(
    id: String? = null,
    number: Int?,
    override val lessons: List<GroupBasedLesson>,
    start: LocalDate,
    end: LocalDate,
    type: InternalTimetableType,
    created: LocalDateTime = LocalDateTime.now(),
    updated: LocalDateTime = LocalDateTime.now(),
) : InternalTimetable(
    id = id,
    number = number,
    lessons = lessons,
    start = start,
    end = end,
    type = type,
    created = created,
    updated = updated,
) {
    fun copy(
        id: String? = this.id,
        number: Int? = this.number,
        lessons: List<GroupBasedLesson> = this.lessons,
        start: LocalDate = this.start,
        end: LocalDate = this.end,
        type: InternalTimetableType = this.type,
        created: LocalDateTime = this.created,
        updated: LocalDateTime = this.updated,
    ): ExcelTimetable {
        return ExcelTimetable(
            id = id,
            number = number,
            lessons = lessons,
            start = start,
            end = end,
            type = type,
            created = created,
            updated = updated,
        )
    }

    fun updateTimestamp(
        updated: LocalDateTime = LocalDateTime.now(),
    ): ExcelTimetable = this.copy(
        lessons = this.lessons,
        updated = updated
    )
}
