package com.melowetty.hsepermhelper.models

import com.melowetty.hsepermhelper.models.v2.LessonV2
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
class LessonTest {
    @Test
    fun `lesson hashcode equals test and equals test`() {
        val expected = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 2,
            lecturer = null,
            office = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val actual = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 2,
            lecturer = null,
            office = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val notEqual = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 3,
            lecturer = null,
            office = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        Assert.isTrue(expected.hashCode() == actual.hashCode(), "Lesson hashcodes are not equals")
        Assert.isTrue(expected == actual, "Lessons are not equals")
        Assert.isTrue(notEqual.hashCode() != actual.hashCode(), "Lesson which are different hashcodes are equals")
        Assert.isTrue(notEqual != actual, "Lessons which are different are equals")
    }

    @Test
    fun `lesson hashcode not equals test and not equals lessons test`() {
        val expected = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 2,
            lecturer = null,
            office = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val firstActual = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 3,
            lecturer = null,
            office = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val secondActual = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 3,
            lecturer = null,
            office = "501",
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        Assert.isTrue(firstActual.hashCode() != expected.hashCode(), "Lesson which are different hashcodes are equals")
        Assert.isTrue(firstActual != expected, "Lessons which are different are equals")
        Assert.isTrue(secondActual.hashCode() != expected.hashCode(), "Lesson which are different hashcodes are equals")
        Assert.isTrue(secondActual != expected, "Lessons which are different are equals")
    }

    @Test
    fun `lessons list equal test`() {
        val firstLesson = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 2,
            lecturer = null,
            office = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val secondLesson = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 3,
            lecturer = null,
            office = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val thirdLesson = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 3,
            lecturer = null,
            office = "501",
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val fourthLesson = LessonV2(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            date = LocalDate.of(2023, 10, 24),
            startTimeStr = "8:10",
            endTimeStr = "9:40",
            startTime = LocalDateTime.of(2023, 10, 24, 8, 10),
            endTime = LocalDateTime.of(2023, 10, 24, 9, 40),
            building = 3,
            lecturer = null,
            office = "502",
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        Assert.isTrue(arrayOf(firstLesson, secondLesson).toHashSet() == arrayOf(secondLesson, firstLesson).toHashSet(), "Lessons list are not equals but they are equals")
        Assert.isTrue(arrayOf(thirdLesson, fourthLesson).toHashSet() != arrayOf(thirdLesson, secondLesson).toHashSet(), "Lessons list are equals but they are not equals")
    }
}