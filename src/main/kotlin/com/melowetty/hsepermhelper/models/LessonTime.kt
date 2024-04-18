package com.melowetty.hsepermhelper.models

abstract class LessonTime(
    open val startTime: String,
    open val endTime: String,
) : Comparable<LessonTime>