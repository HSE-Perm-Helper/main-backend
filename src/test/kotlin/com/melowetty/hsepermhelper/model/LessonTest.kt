package com.melowetty.hsepermhelper.model

import org.junit.jupiter.api.Test
import org.springframework.util.Assert
import java.time.LocalDate

class LessonTest {
    @Test
    fun `lesson hashcode equals test and equals test`() {
        val expected = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 2, office = null)
            ),
            lecturer = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val actual = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 2, office = null)
            ),
            lecturer = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val notEqual = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 3, office = null)
            ),
            lecturer = null,
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
        val expected = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 2, office = null)
            ),
            lecturer = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val firstActual = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 3, office = null)
            ),
            lecturer = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val secondActual = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 3, office = "501")
            ),
            lecturer = null,
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
        val firstLesson = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 2, office = null)
            ),
            lecturer = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val secondLesson = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 3, office = null)
            ),
            lecturer = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val thirdLesson = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 3, office = "501")
            ),
            lecturer = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        val fourthLesson = Lesson(
            subject = "Программирование",
            course = 1,
            programme = "РИС",
            group = "РИС-22-3",
            subGroup = 5,
            time = ScheduledTime(
                date = LocalDate.of(2023, 10, 24),
                startTime = "8:10",
                endTime = "9:40",
            ),
            places = listOf(
                LessonPlace(building = 3, office = "502")
            ),
            lecturer = null,
            lessonType = LessonType.SEMINAR,
            parentScheduleType = ScheduleType.QUARTER_SCHEDULE
        )
        Assert.isTrue(arrayOf(firstLesson, secondLesson).toHashSet() == arrayOf(secondLesson, firstLesson).toHashSet(), "Lessons list are not equals but they are equals")
        Assert.isTrue(arrayOf(thirdLesson, fourthLesson).toHashSet() != arrayOf(thirdLesson, secondLesson).toHashSet(), "Lessons list are equals but they are not equals")
    }
}