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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InternalLesson) return false

        if (subject != other.subject) return false
        if (subGroup != other.subGroup) return false
        if (time != other.time) return false
        if (lecturer != other.lecturer) return false
        if (places != other.places) return false
        if (links != other.links) return false
        if (additionalInfo != other.additionalInfo) return false
        if (lessonType != other.lessonType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subject.hashCode()
        result = 31 * result + (subGroup ?: 0)
        result = 31 * result + time.hashCode()
        result = 31 * result + (lecturer?.hashCode() ?: 0)
        result = 31 * result + (places?.hashCode() ?: 0)
        result = 31 * result + (links?.hashCode() ?: 0)
        result = 31 * result + (additionalInfo?.hashCode() ?: 0)
        result = 31 * result + lessonType.name.hashCode()
        return result
    }


}
