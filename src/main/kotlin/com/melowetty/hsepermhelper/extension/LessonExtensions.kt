package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.domain.model.hseapp.HseAppLesson
import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.excel.model.ExcelLesson
import com.melowetty.hsepermhelper.util.DateUtils.Companion.asStr
import com.melowetty.hsepermhelper.util.DateUtils.Companion.fromGmtToPermTime

class LessonExtensions {
    companion object {
        fun ExcelLesson.toLesson(): Lesson {
            return Lesson(
                subject = subject,
                subGroup = subGroup,
                time = time,
                lecturer = lecturer,
                places = places,
                links = links,
                additionalInfo = additionalInfo,
                lessonType = lessonType,
                parentScheduleType = ScheduleType.WEEK_SCHEDULE,
            )
        }

        fun HseAppLesson.toLesson(): Lesson {
            val startTime = dateStart.fromGmtToPermTime().asStr()
            val endTime = dateEnd.fromGmtToPermTime().asStr()

            return Lesson(
                subject = subject,
                subGroup = null,
                time = ScheduledTime(
                    dateStart.dayOfWeek,
                    dateStart.toLocalDate(),
                    startTime,
                    endTime
                ),
                lecturer = lecturers.firstOrNull(),
                lessonType = type,
                links = streamLinks,
                additionalInfo = note?.let { listOf(note) },
                parentScheduleType = ScheduleType.WEEK_SCHEDULE
            )
        }
    }
}