package com.melowetty.hsepermhelper.domain.model.timetable

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class InternalTimetableInfo(
    val id: String,
    val number: Int?,
    val start: LocalDate,
    val end: LocalDate,
    val type: InternalTimetableType,
    val educationType: EducationType,
    @JsonProperty("is_parent")
    val isParent: Boolean,
    val lessonsHash: Int,
    val source: InternalTimetableSource,
)
