package com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.online

import com.melowetty.hsepermhelper.domain.model.lesson.LessonTime
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableProcessor
import com.melowetty.hsepermhelper.timetable.model.EducationType
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableSource
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.stereotype.Component
import java.time.LocalDate
import kotlin.random.Random

@Component
class OnlineBachelorTimetableProcessor : ExcelTimetableProcessor {
    override fun process(data: Workbook): List<ExcelTimetable> {
        return listOf(
            ExcelTimetable(
                number = 1,
                lessons = listOf(
                    GroupBasedLesson(
                        subject = "Test",
                        group = "РИСБ-25-1",
                        subGroup = 1,
                        time = LessonTime.ofScheduled(LocalDate.now(), "10:00", "12:00"),
                        lecturer = "Михайлов",
                        places = listOf(),
                        links = listOf(),
                        additionalInfo = listOf(),
                        lessonType = LessonType.CONSULT,
                    )
                ),
                start = LocalDate.now(),
                end = LocalDate.now().plusDays(Random.nextLong(1000)),
                type = InternalTimetableType.BACHELOR_WEEK_TIMETABLE,
                educationType = EducationType.BACHELOR_ONLINE,
                isParent = true,
                source = InternalTimetableSource.EXCEL,
            )
        )
    }

    override fun isParseable(name: String): Boolean {
        return name.contains("ОП")
    }
}