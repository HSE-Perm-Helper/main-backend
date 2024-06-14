package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.model.*
import org.hibernate.validator.internal.util.Contracts.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ScheduleUtilsTest {
    @Test
    fun `test merge session schedule`() {
        val lessons = listOf(getLesson(), getLesson(), getLesson(), getLesson(), getLesson())
        val start = LocalDate.now()
        val end = LocalDate.now()
        val actual = ScheduleUtils.mergeSessionSchedules(listOf(
            Schedule(
                start = start,
                end = end,
                scheduleType = ScheduleType.SESSION_SCHEDULE,
                lessons = lessons.subList(0, 3),
                number = 1,
            ),
            Schedule(
                start = end,
                end = end,
                scheduleType = ScheduleType.SESSION_SCHEDULE,
                lessons = lessons.subList(3, 5),
                number = 2,
            )
        ))

        val expected = Schedule(
            start = start,
            end = end,
            scheduleType = ScheduleType.SESSION_SCHEDULE,
            lessons = lessons,
            number = 1,
        )

        assertEquals(expected, actual, "Расписания после мёржа не равны!")
    }

    @Test
    fun `test normalize schedules when do not contains session schedule`() {
        val schedules = listOf(getSchedule(), getSchedule())
        val actual = ScheduleUtils.normalizeSchedules(schedules)

        assertEquals(schedules, actual, "Расписания после нормализации не равны!")
    }

    @Test
    fun `test normalize schedules when contains session schedule`() {
        val schedules = listOf(getSchedule(), getSchedule(),
            getSchedule().copy(scheduleType = ScheduleType.SESSION_SCHEDULE),
            getSchedule().copy(scheduleType = ScheduleType.SESSION_SCHEDULE))
        val actual = ScheduleUtils.normalizeSchedules(schedules)

        assertTrue(actual.size == 3, "Расписания после нормализации содержит больше чем три расписания!")
    }

    private fun getLesson(): Lesson {
        return Lesson(
            course = 1,
            group = "test",
            lecturer = "test",
            lessonType = LessonType.TEST,
            parentScheduleType = ScheduleType.SESSION_SCHEDULE,
            programme = "test",
            subject = "test",
            subGroup = null,
            time = ScheduledTime(
                date = LocalDate.now(),
                startTime = "9:40",
                endTime = "11:00"
            )
        )
    }

    private fun getSchedule(): Schedule {
        return Schedule(
            start = LocalDate.now(),
            end = LocalDate.now(),
            scheduleType = ScheduleType.WEEK_SCHEDULE,
            lessons = listOf(getLesson(), getLesson(), getLesson(), getLesson()),
            number = 1,
        )
    }
}