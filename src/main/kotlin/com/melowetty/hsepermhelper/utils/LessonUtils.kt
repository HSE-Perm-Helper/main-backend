package com.melowetty.hsepermhelper.utils

import com.melowetty.hsepermhelper.models.Lesson

class LessonUtils {
    companion object {
        /**
         * Removes similar lessons which differ only source
         *
         * @param lessons list of lessons
         * @return cleared list of lessons
         */
        fun clearRepeats(lessons: List<Lesson>): List<Lesson> {
            val lessonsWithoutRepeats = mutableListOf<Lesson>()
            lessons.forEach { lesson ->
                if (lessonsWithoutRepeats.contains(lesson).not()) {
                    val lessonWithMaxPriory = lessons.filter { filteringLesson ->
                        lesson == filteringLesson
                    }.maxByOrNull {
                        it.parentScheduleType.priority
                    }
                    if (lessonWithMaxPriory != null) {
                        lessonsWithoutRepeats.add(lessonWithMaxPriory)
                    }
                }
            }
            return lessonsWithoutRepeats
        }
    }
}