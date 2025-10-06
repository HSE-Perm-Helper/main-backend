package com.melowetty.hsepermhelper.timetable.model

import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import java.time.LocalDate

class ExcelTimetable(
    id: String? = null,
    number: Int?,
    override val lessons: List<GroupBasedLesson>,
    start: LocalDate,
    end: LocalDate,
    type: InternalTimetableType,
) : InternalTimetable(
    id = id,
    number = number,
    lessons = lessons,
    start = start,
    end = end,
    type = type,
) {
    fun copy(
        id: String? = this.id,
        number: Int? = this.number,
        lessons: List<GroupBasedLesson> = this.lessons,
        start: LocalDate = this.start,
        end: LocalDate = this.end,
        type: InternalTimetableType = this.type,
    ): ExcelTimetable {
        return ExcelTimetable(
            id = id,
            number = number,
            lessons = lessons,
            start = start,
            end = end,
            type = type,
        )
    }
}
