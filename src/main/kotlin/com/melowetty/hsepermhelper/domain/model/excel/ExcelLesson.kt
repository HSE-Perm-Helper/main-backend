package com.melowetty.hsepermhelper.domain.model.excel

import com.melowetty.hsepermhelper.model.lesson.LessonPlace
import com.melowetty.hsepermhelper.model.lesson.LessonTime
import com.melowetty.hsepermhelper.model.lesson.LessonType

data class ExcelLesson(
    val subject: String,
    val course: Int,
    val programme: String,
    val group: String,
    val subGroup: Int?,
    val time: LessonTime,
    val lecturer: String?,
    val places: List<LessonPlace>? = null,
    val links: List<String>? = null,
    val additionalInfo: List<String>? = null,
    val lessonType: LessonType,
) : Comparable<ExcelLesson> {
    override fun compareTo(other: ExcelLesson): Int {
        return time.compareTo(other.time)
    }
}
