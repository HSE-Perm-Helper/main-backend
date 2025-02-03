package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.dto.HideLessonDto
import com.melowetty.hsepermhelper.domain.dto.SettingsDto
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.model.excel.ExcelLesson
import com.melowetty.hsepermhelper.model.excel.ExcelSchedule
import com.melowetty.hsepermhelper.model.lesson.Lesson
import com.melowetty.hsepermhelper.model.lesson.LessonType
import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.repository.ExcelScheduleRepository
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
    private lateinit var scheduleService: ExcelScheduleService

    @Mock
    private lateinit var scheduleRepository: ExcelScheduleRepository

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var notificationService: NotificationService

    @Test
    fun `normal filter user schedule with hidden lessons`() {
        val user = UserDto(
            settings = SettingsDto(
                group = "РИС-24-1",
                hiddenLessons = setOf(
                    HideLessonDto(id = 1, lesson = "Test Hidden", LessonType.TEST, subGroup = 1),
                    HideLessonDto(id = 2, lesson = "Test Hidden", LessonType.SEMINAR, subGroup = null)
                )
            )
        )

        val lesson = ExcelLesson(
            subject = "Normal lesson", course = 1, programme = "РИС", "РИС-24-1", subGroup = 1, time = ScheduledTime(
                DayOfWeek.MONDAY, LocalDate.now(), "11:00", "12:30"
            ), lecturer = "test",
            lessonType = LessonType.SEMINAR
        )

        val schedule = ExcelSchedule(
            number = 0,
            scheduleType = ScheduleType.WEEK_SCHEDULE,
            start = LocalDate.now(),
            end = LocalDate.now(),
            lessons = listOf(
                lesson,
                lesson.copy(subject = "Test Hidden", lessonType = LessonType.TEST),
                lesson.copy(subject = "Test Hidden", subGroup = null),
                lesson.copy(subject = "Test Hidden", subGroup = 1, lessonType = LessonType.SEMINAR)
            )
        )

        val actual = scheduleService.filterSchedule(schedule, user).lessons.toHashSet()
        val expected =
            setOf(lesson.copy(), lesson.copy(subject = "Test Hidden", subGroup = 1, lessonType = LessonType.SEMINAR))

        assertEquals(expected, actual)
    }
}