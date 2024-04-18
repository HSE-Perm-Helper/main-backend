package com.melowetty.hsepermhelper.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.melowetty.hsepermhelper.utils.DateUtils
import java.time.LocalTime

abstract class LessonTime(
    open val startTime: String,
    open val endTime: String,
    @JsonIgnore
    val startLocaltime: LocalTime = DateUtils.parseTime(startTime),
    @JsonIgnore
    val endLocaltime: LocalTime = DateUtils.parseTime(endTime)
) : Comparable<LessonTime>