package com.melowetty.hsepermhelper.domain.model.timetable

import com.melowetty.hsepermhelper.domain.model.timetable.impl.GroupBasedLesson
import java.time.LocalDate
import java.time.LocalDateTime

class ExcelTimetable(
    id: String? = null,
    number: Int?,
    override val lessons: List<GroupBasedLesson>,
    start: LocalDate,
    end: LocalDate,
    type: InternalTimetableType,
    educationType: EducationType,
    isParent: Boolean,
    source: InternalTimetableSource,
    created: LocalDateTime = LocalDateTime.now(),
    updated: LocalDateTime = LocalDateTime.now(),
) : InternalTimetable(
    id = id,
    number = number,
    lessons = lessons,
    start = start,
    end = end,
    type = type,
    educationType = educationType,
    isParent = isParent,
    source = source,
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
        educationType: EducationType = this.educationType,
        isParent: Boolean = this.isParent,
        source: InternalTimetableSource = this.source,
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
            educationType = educationType,
            isParent = isParent,
            source = source,
            created = created,
            updated = updated,
        )
    }

}
