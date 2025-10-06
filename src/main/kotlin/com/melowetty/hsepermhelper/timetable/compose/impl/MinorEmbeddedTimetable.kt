package com.melowetty.hsepermhelper.timetable.compose.impl

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.lesson.LessonPlace
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.service.HseAppApiService
import com.melowetty.hsepermhelper.timetable.compose.EmbeddedTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.impl.MinorLesson
import com.melowetty.hsepermhelper.util.DateUtils.Companion.asStr
import com.melowetty.hsepermhelper.util.DateUtils.Companion.fromGmtToPermTime
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class MinorEmbeddedTimetable(
    private val hseAppApiService: HseAppApiService
) : EmbeddedTimetable {
    override fun embed(user: UserDto, timetable: InternalTimetable): InternalTimetable {
        val email = user.email
            ?: run {
                logger.warn { "User email is null" }
                return timetable
            }

        val hseAppLessons = hseAppApiService.getLessons(email, timetable.start, timetable.end)
            .filter { it.isMinor }
            .map {
                val startTime = it.dateStart.fromGmtToPermTime().asStr()
                val endTime = it.dateEnd.fromGmtToPermTime().asStr()

                val places: MutableList<LessonPlace> = mutableListOf()
                if (it.auditorium == HSE_APP_ONLINE_PLACE_DEFINITION) {
                    places.add(LessonPlace(null, 0))
                } else {
                    places.add(LessonPlace(it.auditorium, null))
                }

                MinorLesson(
                    subject = it.subject,
                    time = ScheduledTime(
                        dayOfWeek = it.dateStart.dayOfWeek,
                        it.dateStart.toLocalDate(),
                        startTime,
                        endTime

                    ),
                    lecturer = it.lecturers.firstOrNull(),
                    links = it.streamLinks,
                    additionalInfo = it.note?.let { note -> listOf(note) },
                    lessonType = it.type,
                )
            }

        logger.info { "Added ${hseAppLessons.size} minor lessons" }

        return timetable.copy(
            lessons = (hseAppLessons + timetable.lessons)
                .filterNot { it.lessonType == LessonType.COMMON_MINOR }
                .sorted()
        )
    }

    override fun isEmbeddable(user: UserDto, timetable: InternalTimetable): Boolean {
        return user.email != null &&
            (timetable.type == InternalTimetableType.BACHELOR_WEEK_SCHEDULE
                || timetable.type == InternalTimetableType.BACHELOR_SESSION_SCHEDULE)
    }

    companion object {
        private const val HSE_APP_ONLINE_PLACE_DEFINITION = "Онлайн"

        private val logger = KotlinLogging.logger {  }
    }

}