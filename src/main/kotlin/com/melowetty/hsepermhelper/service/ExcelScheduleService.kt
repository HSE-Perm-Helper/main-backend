package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.extension.LessonExtensions.Companion.toLesson
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toSchedule
import com.melowetty.hsepermhelper.persistence.repository.ExcelScheduleRepository
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import org.springframework.stereotype.Service

@Service
@Deprecated("Stop using when new schedule flow is implemented")
class ExcelScheduleService(
    private val scheduleRepository: ExcelScheduleRepository,
) {
    fun getScheduleByGroup(
        group: String,
    ): List<Lesson> {
        return scheduleRepository.getSchedules().asSequence()
            .filterNot { it.type == InternalTimetableType.BACHELOR_QUARTER_TIMETABLE }
            .map { it.lessons }
            .flatten()
            .mapNotNull { it as? GroupBasedLesson }
            .filter { it.group == group }
            .map { it.toLesson() }
            .sortedBy { it.time }
            .toList()
    }

    private fun filterSchedules(
        schedules: List<InternalTimetable>,
        user: UserDto,
        withoutHiddenLessons: Boolean = true
    ): List<InternalTimetable> {
        val filteredSchedules = schedules.map { schedule ->
            filterSchedule(schedule, user, withoutHiddenLessons)
        }
        return filteredSchedules
    }

    fun filterSchedule(schedule: InternalTimetable, user: UserDto, withoutHiddenLessons: Boolean = true): InternalTimetable {
        val filteredLessons = schedule.lessons
            .mapNotNull { it as? GroupBasedLesson }
            .filter { lesson: GroupBasedLesson ->
            lesson.group == user.settings.group
        }.filter {
            (it.lessonType == LessonType.COMMON_ENGLISH).not()
        }.filter {
            if (withoutHiddenLessons) {
                return@filter user.settings.hiddenLessons.any { hideLessonEntity ->
                    hideLessonEntity.lesson == it.subject
                            && hideLessonEntity.lessonType == it.lessonType
                            && hideLessonEntity.subGroup == it.subGroup

                }.not()
            }
            return@filter true
        }

        return schedule.copy(
            lessons = filteredLessons
        )
    }

    fun getUserSchedules(user: UserDto): List<Schedule> {
        return filterSchedules(scheduleRepository.getSchedules(), user).map { it.toSchedule() }
    }

    fun getAvailableCourses(): List<Int> {
        return scheduleRepository.getAvailableCourses()
    }

    fun getAvailablePrograms(course: Int): List<String> {
        return scheduleRepository.getAvailablePrograms(course = course)
    }

    fun getAvailableGroups(course: Int, program: String): List<String> {
        return scheduleRepository.getAvailableGroups(course = course, program = program)
    }
}