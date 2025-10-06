package com.melowetty.hsepermhelper.timetable.model

import com.melowetty.hsepermhelper.domain.model.lesson.LessonPlace
import com.melowetty.hsepermhelper.domain.model.lesson.LessonTime
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType

abstract class InternalLesson(
    open val subject: String,
    open val subGroup: Int?,
    open val time: LessonTime,
    open val lecturer: String?,
    open val places: List<LessonPlace>? = null,
    open val links: List<String>? = null,
    open val additionalInfo: List<String>? = null,
    open val lessonType: LessonType,
) : Comparable<InternalLesson> {
    override fun compareTo(other: InternalLesson): Int {
        return time.compareTo(other.time)
    }
}
