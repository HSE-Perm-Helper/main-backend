package com.melowetty.hsepermhelper.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.melowetty.hsepermhelper.util.DateUtils
import java.time.LocalTime

abstract class LessonTime(
    open val startTime: String,
    open val endTime: String,
    @JsonIgnore
    val startLocaltime: LocalTime = DateUtils.parseTime(startTime),
    @JsonIgnore
    val endLocaltime: LocalTime = DateUtils.parseTime(endTime)
) : Comparable<LessonTime>