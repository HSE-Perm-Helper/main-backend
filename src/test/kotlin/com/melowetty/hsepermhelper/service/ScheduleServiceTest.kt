package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.HideLessonDto
import com.melowetty.hsepermhelper.domain.dto.SettingsDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.model.Lesson
import com.melowetty.hsepermhelper.model.LessonType
import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleType
import com.melowetty.hsepermhelper.model.ScheduledTime
import com.melowetty.hsepermhelper.repository.ScheduleRepository
import com.melowetty.hsepermhelper.service.impl.ScheduleServiceImpl
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ScheduleServiceTest {
    @InjectMocks
    private lateinit var scheduleService: ScheduleServiceImpl

    @Mock
    private lateinit var scheduleRepository: ScheduleRepository

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var notificationService: NotificationService

    @Test
    fun `normal filter user schedule with hidden lessons`() {
        val user = UserDto(
            settings = SettingsDto(
                group = "РИС-24-1",
                subGroup = 1,
                hiddenLessons = setOf(
                    HideLessonDto(id = 1, lesson = "Test Hidden", LessonType.TEST, subGroup = 1),
                    HideLessonDto(id = 2, lesson = "Test Hidden", LessonType.SEMINAR, subGroup = null)
                )
            )
        )

        val lesson = Lesson(
            subject = "Normal lesson", course = 1, programme = "РИС", "РИС-24-1", subGroup = 1, time = ScheduledTime(
                DayOfWeek.MONDAY, LocalDate.now(), "11:00", "12:30"
            ), lecturer = "test",
            lessonType = LessonType.SEMINAR, parentScheduleType = ScheduleType.WEEK_SCHEDULE
        )

        val schedule = Schedule(
            number = 0,
            scheduleType = ScheduleType.WEEK_SCHEDULE,
            start = LocalDate.now(),
            end = LocalDate.now(),
            lessons = listOf(
                lesson, lesson.copy(subject = "Test Hidden", lessonType = LessonType.TEST),
                lesson.copy(subject = "Test Hidden", subGroup = null),
                lesson.copy(subject = "Test Hidden", subGroup = 1, lessonType = LessonType.SEMINAR)
            )
        )

        val actual = scheduleService.filterSchedule(schedule, user).lessons.toHashSet()
        val expected =
            setOf(lesson.copy(subGroup = null), lesson.copy(subject = "Test Hidden", subGroup = null, lessonType = LessonType.SEMINAR))

        assertEquals(expected, actual)
    }

    @Test
    fun `temp fix filter user schedule with hidden lessons`() {
        val user = UserDto(
            settings = SettingsDto(
                group = "РИС-22-1",
                subGroup = 1,
                hiddenLessons = setOf(
                    HideLessonDto(id = 1, lesson = "Test Hidden", LessonType.TEST, subGroup = 1),
                    HideLessonDto(id = 2 ,lesson = "Test Hidden", LessonType.SEMINAR, subGroup = null)
                )
            )
        )

        val lesson = Lesson(
            subject = "Normal lesson", course = 1, programme = "РИС", "РИС-22-1", subGroup = 1, time = ScheduledTime(
                DayOfWeek.MONDAY, LocalDate.now(), "11:00", "12:30"
            ), lecturer = "test",
            lessonType = LessonType.SEMINAR, parentScheduleType = ScheduleType.WEEK_SCHEDULE
        )

        val schedule = Schedule(
            number = 0,
            scheduleType = ScheduleType.WEEK_SCHEDULE,
            start = LocalDate.now(),
            end = LocalDate.now(),
            lessons = listOf(
                lesson, lesson.copy(subject = "Test Hidden", lessonType = LessonType.TEST),
                lesson.copy(subject = "Test Hidden", subGroup = null),
                lesson.copy(subject = "Test Hidden", subGroup = 2, lessonType = LessonType.SEMINAR)
            )
        )

        val actual = scheduleService.filterSchedule(schedule, user).lessons.toHashSet()
        val expected = setOf(
            lesson.copy(subject = lesson.subject),
            lesson.copy(subject = "Test Hidden", subGroup = 2, lessonType = LessonType.SEMINAR)
        )

        assertEquals(expected, actual)
    }
}