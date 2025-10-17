package com.melowetty.hsepermhelper.timetable.model.impl

import com.melowetty.hsepermhelper.domain.model.lesson.LessonPlace
import com.melowetty.hsepermhelper.domain.model.lesson.LessonTime
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.timetable.model.InternalLesson
import java.time.LocalDate

data class GroupBasedLesson(
    override val subject: String,
    val group: String,
    override val subGroup: Int?,
    override val time: LessonTime,
    override val lecturer: String?,
    override val places: List<LessonPlace>? = null,
    override val links: List<String>? = null,
    override val additionalInfo: List<String>? = null,
    override val lessonType: LessonType
) : InternalLesson(subject, subGroup, time, lecturer, places, links, additionalInfo, lessonType) {
    fun program() = group.split("-").first()

    fun course(): Int {
        val year = group.split("-")[1].toInt()
        val curDate = LocalDate.now()
        val curYear = curDate.year % 100

        if (curDate.monthValue < 9) {
            return curYear - year
        } else {
            return curYear - year + 1
        }
    }
}