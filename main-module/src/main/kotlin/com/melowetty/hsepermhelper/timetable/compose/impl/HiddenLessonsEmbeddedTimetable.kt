package com.melowetty.hsepermhelper.timetable.compose.impl

import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.timetable.compose.EmbeddedTimetable
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetable
import com.melowetty.hsepermhelper.domain.model.timetable.TimetableContext
import com.melowetty.hsepermhelper.domain.model.timetable.TimetablePurpose
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class HiddenLessonsEmbeddedTimetable: EmbeddedTimetable {
    override fun embed(user: UserRecord, timetable: InternalTimetable): InternalTimetable {
        val hiddenLessons = user.hiddenLessons.map { hideLesson ->
            "${hideLesson.lesson} ${hideLesson.lessonType} ${hideLesson.subGroup}"
        }.toSet()

        val lessons = timetable.lessons.filter {
            val lessonAsStr = "${it.subject} ${it.lessonType} ${it.subGroup}"
            return@filter (lessonAsStr in hiddenLessons).not()
        }

        logger.info { "Hidden ${timetable.lessons.size - lessons.size} lessons" }

        return timetable.copy(lessons = lessons)
    }

    override fun isEmbeddable(user: UserRecord, timetable: InternalTimetable, context: TimetableContext): Boolean {
        return context.purpose != TimetablePurpose.SETTINGS
                && user.hiddenLessons.isNotEmpty()
    }

    override fun priority(): Int {
        return -1
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}