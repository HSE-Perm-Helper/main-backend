package com.melowetty.hsepermhelper.timetable.compose.impl

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.timetable.compose.EmbeddedTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class HiddenLessonsEmbeddedTimetable: EmbeddedTimetable {
    override fun embed(user: UserDto, timetable: InternalTimetable): InternalTimetable {
        val hiddenLessons = user.settings.hiddenLessons.map { hideLesson ->
            "${hideLesson.lesson} ${hideLesson.lessonType} ${hideLesson.subGroup}"
        }.toSet()

        val lessons = timetable.lessons.filter {
            val lessonAsStr = "${it.subject} ${it.lessonType} ${it.subGroup}"
            return@filter (lessonAsStr in hiddenLessons).not()
        }

        logger.info { "Hidden ${timetable.lessons.size - lessons.size} lessons" }

        return timetable.copy(lessons = lessons)
    }

    override fun isEmbeddable(user: UserDto, timetable: InternalTimetable): Boolean {
        return user.settings.hiddenLessons.isNotEmpty()
    }

    override fun priority(): Int {
        return -1
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}