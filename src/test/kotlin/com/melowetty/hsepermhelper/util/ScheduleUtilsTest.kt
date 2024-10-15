package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.model.Lesson
import com.melowetty.hsepermhelper.model.LessonType
import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleType
import com.melowetty.hsepermhelper.model.ScheduledTime
import com.melowetty.hsepermhelper.util.ScheduleUtils.Companion.filterWeekSchedules
import org.hibernate.validator.internal.util.Contracts.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ScheduleUtilsTest {
    @Test
    fun `test merge session schedule`() {
        val lessons = listOf(getLesson(), getLesson(), getLesson(), getLesson(), getLesson())
        val start = LocalDate.now()
        val end = LocalDate.now()
        val actual = ScheduleUtils.mergeSessionSchedules(
            listOf(
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
            )
        )

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
        val schedules = listOf(
            getSchedule(), getSchedule(),
            getSchedule().copy(scheduleType = ScheduleType.SESSION_SCHEDULE),
            getSchedule().copy(scheduleType = ScheduleType.SESSION_SCHEDULE)
        )
        val actual = ScheduleUtils.normalizeSchedules(schedules)

        assertTrue(actual.size == 3, "Расписания после нормализации содержит больше чем три расписания!")
    }

    @Test
    fun getWeekScheduleByDate_returnsCorrectSchedule() {
        val schedules = listOf(getSchedule(), getSchedule(), getSchedule())
        val date = LocalDate.now()
        val actual = ScheduleUtils.getWeekScheduleByDate(schedules, date)
        assertEquals(schedules[0], actual, "Returned schedule does not match the expected schedule!")
    }

    @Test
    fun getWeekScheduleByDate_handlesNoMatchingSchedule() {
        val schedules = listOf(getSchedule(), getSchedule(), getSchedule())
        val date = LocalDate.now().plusDays(10)
        assertNull(ScheduleUtils.getWeekScheduleByDate(schedules, date))
    }

    @Test
    fun getLessonsAtDateInWeekSchedule_returnsCorrectLessons() {
        val schedule = getSchedule()
        val date = LocalDate.now()
        val actual = ScheduleUtils.getLessonsAtDateInWeekSchedule(schedule, date)
        assertEquals(schedule.lessons, actual, "Returned lessons do not match the expected lessons!")
    }

    @Test
    fun getLessonsAtDateInWeekSchedule_handlesNoLessonsOnDate() {
        val schedule = getSchedule()
        val date = LocalDate.now().plusDays(10)
        val actual = ScheduleUtils.getLessonsAtDateInWeekSchedule(schedule, date)
        assertTrue(actual.isEmpty(), "Expected no lessons on the given date!")
    }

    @Test
    fun filterWeekSchedules_returnsOnlyWeekAndSessionSchedules() {
        val schedules = listOf(
            getSchedule().copy(scheduleType = ScheduleType.WEEK_SCHEDULE),
            getSchedule().copy(scheduleType = ScheduleType.SESSION_SCHEDULE),
            getSchedule().copy(scheduleType = ScheduleType.QUARTER_SCHEDULE)
        )
        val actual = schedules.filterWeekSchedules()
        assertEquals(2, actual.size, "Expected only week and session schedules!")
        assertTrue(
            actual.all { it.scheduleType == ScheduleType.WEEK_SCHEDULE || it.scheduleType == ScheduleType.SESSION_SCHEDULE },
            "Expected only week and session schedules!"
        )
    }

    @Test
    fun filterWeekSchedules_handlesEmptyList() {
        val schedules = emptyList<Schedule>()
        val actual = schedules.filterWeekSchedules()
        assertTrue(actual.isEmpty(), "Expected no schedules in the result!")
    }

    @Test
    fun filterWeekSchedules_handlesNoMatchingSchedules() {
        val schedules = listOf(
            getSchedule().copy(scheduleType = ScheduleType.QUARTER_SCHEDULE),
            getSchedule().copy(scheduleType = ScheduleType.QUARTER_SCHEDULE)
        )
        val actual = schedules.filterWeekSchedules()
        assertTrue(actual.isEmpty(), "Expected no schedules in the result!")
    }

    @Test
    fun getCourseFromGroup() {
        val actual = ScheduleUtils.getCourseFromGroup("РИС-22-3")
        val expected = 3

        assertEquals(expected, actual)
    }

    @Test
    fun getShortGroupFromGroup() {
        val actual = ScheduleUtils.getShortGroupFromGroup("ИЯ-22-1")
        val expected = "ИЯ"

        assertEquals(expected, actual)
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