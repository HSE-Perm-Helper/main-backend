package com.melowetty.hsepermhelper.timetable.model.impl

import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.timetable.model.InternalLesson

data class MinorLesson(
    override val subject: String,
    override val time: ScheduledTime,
    override val lecturer: String?,
    override val links: List<String>? = null,
    override val additionalInfo: List<String>? = null,
    override val lessonType: LessonType
) : InternalLesson(subject, null, time, lecturer, null, links, additionalInfo, lessonType)
