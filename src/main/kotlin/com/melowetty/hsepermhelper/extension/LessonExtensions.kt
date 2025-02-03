package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.model.excel.ExcelLesson
import com.melowetty.hsepermhelper.model.lesson.Lesson
import com.melowetty.hsepermhelper.model.schedule.ScheduleType

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
    }
}