package com.melowetty.hsepermhelper.domain.model.lesson

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.melowetty.hsepermhelper.util.DateUtils
import java.time.DayOfWeek
import java.time.LocalTime

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ScheduledTime::class, name = "SCHEDULED_TIME"),
    JsonSubTypes.Type(value = CycleTime::class, name = "CYCLE_TIME")
)
abstract class LessonTime(
    open val dayOfWeek: DayOfWeek,
    open val startTime: String,
    open val endTime: String,
    @JsonIgnore
    val startLocaltime: LocalTime = DateUtils.parseTime(startTime),
    @JsonIgnore
    val endLocaltime: LocalTime = DateUtils.parseTime(endTime)
) : Comparable<LessonTime>