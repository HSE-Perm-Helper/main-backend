package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.excel.model.ExcelLesson
import com.melowetty.hsepermhelper.excel.model.ExcelSchedule
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.DayOfWeek
import java.time.LocalDate

class TestUtils {
    companion object {
        fun readFile(file: String): File {
            return File(
                data = readFileAsInputStream(file).readAllBytes(),
                name = file
            )
        }

        fun readFileAsInputStream(file: String): InputStream {
            return Files.newInputStream(Path.of("src/test/resources/$file"))
        }

        fun getSchedule(): ExcelSchedule {
            return ExcelSchedule(
                number = 1,
                scheduleType = ScheduleType.WEEK_SCHEDULE,
                start = LocalDate.now(),
                end = LocalDate.now(),
                lessons = listOf(getLesson(), getLesson(), getLesson(), getLesson())
            )
        }

        private fun getLesson(): ExcelLesson {
            return ExcelLesson(
                course = 1,
                programme = "РИС",
                group = "РИС-22-3",
                lecturer = "Мыльников",
                subject = "Программирование",
                lessonType = LessonType.TEST,
                time = ScheduledTime(
                    dayOfWeek = DayOfWeek.MONDAY,
                    date = LocalDate.now(),
                    startTime = "9:40",
                    endTime = "11:00"
                ),
                subGroup = 1
            )
        }
    }
}