package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.domain.model.lesson.CycleTime
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.online.OnlineTimetableCellParser
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.CellInfo
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedCellInfo
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class OnlineTimetableCellParserTest {

    private val scheduleInfo = ParsedScheduleInfo(
        number = 1,
        startDate = LocalDate.of(2026, 1, 1),
        endDate = LocalDate.of(2026, 6, 30),
        type = InternalTimetableType.BACHELOR_WEEK_TIMETABLE,
    )

    private val lessonTime = CycleTime(DayOfWeek.MONDAY, "11:10", "12:30")

    private fun cellInfo(value: String, isUnderlined: Boolean = false, group: String = "РИСБ-25-1") =
        ParsedCellInfo(
            scheduleInfo = scheduleInfo,
            cellInfo = CellInfo(
                value = value,
                isUnderlined = isUnderlined,
                group = group,
                time = lessonTime,
            )
        )

    @Test
    fun `should parse subject, lecturer and link`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Правовая грамотность\nПанкова Д.Ю.\nhttps://zoom.us/j/12345")
        )

        assertThat(lessons).hasSize(1)
        val lesson = lessons.first()
        assertThat(lesson.subject).isEqualTo("Правовая грамотность")
        assertThat(lesson.lecturer).isEqualTo("Панкова Д.Ю.")
        assertThat(lesson.links).containsExactly("https://zoom.us/j/12345")
        assertThat(lesson.group).isEqualTo("РИСБ-25-1")
    }

    @Test
    fun `should return empty list for empty cell`() {
        assertThat(OnlineTimetableCellParser.parseLesson(cellInfo(""))).isEmpty()
    }

    @Test
    fun `should return empty list for blank-only lines`() {
        assertThat(OnlineTimetableCellParser.parseLesson(cellInfo("\n   \n  "))).isEmpty()
    }

    @Test
    fun `should return empty list when subject contains сессия`() {
        assertThat(OnlineTimetableCellParser.parseLesson(cellInfo("Сессия\nПанкова Д.Ю."))).isEmpty()
    }

    @Test
    fun `should return empty list when subject contains сессия in uppercase`() {
        assertThat(OnlineTimetableCellParser.parseLesson(cellInfo("СЕССИЯ\nПанкова Д.Ю."))).isEmpty()
    }

    @Test
    fun `should set lecturer null and links null when only subject present`() {
        val lessons = OnlineTimetableCellParser.parseLesson(cellInfo("Математический анализ"))

        assertThat(lessons).hasSize(1)
        val lesson = lessons.first()
        assertThat(lesson.lecturer).isNull()
        assertThat(lesson.links).isNull()
    }

    @Test
    fun `should set links null when no links present`() {
        val lessons = OnlineTimetableCellParser.parseLesson(cellInfo("Математический анализ\nИванов И.И."))

        assertThat(lessons.first().links).isNull()
    }

    @Test
    fun `should collect multiple links`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Математический анализ\nИванов И.И.\nhttps://zoom.us/j/111\nhttps://teams.microsoft.com/join/222")
        )

        assertThat(lessons.first().links).containsExactly(
            "https://zoom.us/j/111",
            "https://teams.microsoft.com/join/222",
        )
    }

    @Test
    fun `should trim whitespace from lines`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("  Математический анализ  \n  Иванов И.И.  \n  https://zoom.us/j/111  ")
        )

        assertThat(lessons).hasSize(1)
        val lesson = lessons.first()
        assertThat(lesson.subject).isEqualTo("Математический анализ")
        assertThat(lesson.lecturer).isEqualTo("Иванов И.И.")
        assertThat(lesson.links).containsExactly("https://zoom.us/j/111")
    }

    @Test
    fun `should use EVENT type when no lecturer`() {
        val lessons = OnlineTimetableCellParser.parseLesson(cellInfo("Математический анализ"))

        assertThat(lessons.first().lessonType).isEqualTo(LessonType.EVENT)
    }

    @Test
    fun `should use SEMINAR type when lecturer present and not underlined`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Математический анализ\nИванов И.И.", isUnderlined = false)
        )

        assertThat(lessons.first().lessonType).isEqualTo(LessonType.SEMINAR)
    }

    @Test
    fun `should use LECTURE type when lecturer present and cell is underlined`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Математический анализ\nИванов И.И.", isUnderlined = true)
        )

        assertThat(lessons.first().lessonType).isEqualTo(LessonType.LECTURE)
    }

    @Test
    fun `should detect LECTURE type from subject`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Математический анализ (лекция)\nИванов И.И.")
        )

        assertThat(lessons.first().lessonType).isEqualTo(LessonType.LECTURE)
    }

    @Test
    fun `should detect EXAM type from subject`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Экзамен по математике\nИванов И.И.")
        )

        assertThat(lessons.first().lessonType).isEqualTo(LessonType.EXAM)
    }

    @Test
    fun `should detect TEST type from subject`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Зачёт по математике\nИванов И.И.")
        )

        assertThat(lessons.first().lessonType).isEqualTo(LessonType.TEST)
    }

    @Test
    fun `should preserve group from cell`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Математический анализ\nИванов И.И.", group = "ПМИ-24-1")
        )

        assertThat(lessons.first().group).isEqualTo("ПМИ-24-1")
    }

    @Test
    fun `should extract lecturer embedded in subject line`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Правовая грамотность Панкова Д.Ю.\nhttps://zoom.us/j/12345")
        )

        assertThat(lessons).hasSize(1)
        val lesson = lessons.first()
        assertThat(lesson.subject).isEqualTo("Правовая грамотность")
        assertThat(lesson.lecturer).isEqualTo("Панкова Д.Ю.")
        assertThat(lesson.links).containsExactly("https://zoom.us/j/12345")
    }

    @Test
    fun `should extract lecturer embedded in subject line without link`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("История России Василенко Ю.В.")
        )

        assertThat(lessons).hasSize(1)
        val lesson = lessons.first()
        assertThat(lesson.subject).isEqualTo("История России")
        assertThat(lesson.lecturer).isEqualTo("Василенко Ю.В.")
        assertThat(lesson.links).isNull()
    }

    @Test
    fun `should not extract lecturer when subject has no name pattern`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Математический анализ")
        )

        assertThat(lessons.first().lecturer).isNull()
        assertThat(lessons.first().subject).isEqualTo("Математический анализ")
    }

    @Test
    fun `should prefer separate line over embedded when both present`() {
        val lessons = OnlineTimetableCellParser.parseLesson(
            cellInfo("Правовая грамотность Панкова Д.Ю.\nИванов И.И.\nhttps://zoom.us/j/12345")
        )

        assertThat(lessons.first().lecturer).isEqualTo("Иванов И.И.")
        assertThat(lessons.first().subject).isEqualTo("Правовая грамотность Панкова Д.Ю.")
    }
}
