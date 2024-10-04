package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.model.File
import com.melowetty.hsepermhelper.model.Lesson
import com.melowetty.hsepermhelper.model.LessonType
import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleType
import com.melowetty.hsepermhelper.model.ScheduledTime
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate

class TestUtils {
    companion object {
        fun readFile(file: String): File {
            return File(data = readFileAsInputStream(file).readAllBytes())
        }

        fun readFileAsInputStream(file: String): InputStream {
            return Files.newInputStream(Path.of("src/test/resources/$file"))
        }

        fun getSchedule(): Schedule {
            return Schedule(
                number = 1,
                scheduleType = ScheduleType.WEEK_SCHEDULE,
                start = LocalDate.now(),
                end = LocalDate.now(),
                lessons = listOf(getLesson(), getLesson(), getLesson(), getLesson())
            )
        }

        fun getLesson(): Lesson {
            return Lesson(
                course = 1,
                programme = "РИС",
                group = "РИС-22-3",
                lecturer = "Мыльников",
                subject = "Программирование",
                lessonType = LessonType.TEST,
                time = ScheduledTime(
                    date = LocalDate.now(),
                    startTime = "9:40",
                    endTime = "11:00"
                ),
                parentScheduleType = ScheduleType.WEEK_SCHEDULE,
                subGroup = 1
            )
        }
    }
}