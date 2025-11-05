package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.excel.impl.HseTimetableExcelParserImpl
import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.BachelorTimetableSheetExcelParser
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class HseTimetableExcelParserTest {
    @InjectMockKs
    private lateinit var parser: HseTimetableExcelParserImpl

    @MockK
    private lateinit var sheetParser: BachelorTimetableSheetExcelParser

    @MockK
    private lateinit var notificationService: NotificationService

    @Test
    fun `get basic schedule info`() {
        val header = "на 1 неделю (с 12.12.2024 по 19.12.2024)"

        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = 1,
            startDate = LocalDate.of(2024, 12, 12),
            endDate = LocalDate.of(2024, 12, 19),
            type = InternalTimetableType.BACHELOR_WEEK_TIMETABLE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get session schedule info`() {
        val header = "на сессию (с 01.11.2024 по 19.12.2024)"

        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = null,
            startDate = LocalDate.of(2024, 11, 1),
            endDate = LocalDate.of(2024, 12, 19),
            type = InternalTimetableType.BACHELOR_SESSION_TIMETABLE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get quarter schedule info`() {
        val header = "на 2 модуль (с 01.11.2024 по 19.12.2024)"

        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = 2,
            startDate = LocalDate.of(2024, 11, 1),
            endDate = LocalDate.of(2024, 12, 19),
            type = InternalTimetableType.BACHELOR_QUARTER_TIMETABLE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get schedule info when wrong format for dates`() {
        val header = "на 2 модуль (с 01.11.20.24 по 19.12.2024)"

        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = 2,
            startDate = LocalDate.of(2024, 11, 1),
            endDate = LocalDate.of(2024, 12, 19),
            type = InternalTimetableType.BACHELOR_QUARTER_TIMETABLE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get schedule info when get week number and one day`() {
        val header = "на 6 неделю (19.12.2024)"

        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = 6,
            startDate = LocalDate.of(2024, 12, 19),
            endDate = LocalDate.of(2024, 12, 19),
            type = InternalTimetableType.BACHELOR_WEEK_TIMETABLE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get schedule info when no number week and one day`() {
        val header = "на сессию (19.12.2024)"

        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = null,
            startDate = LocalDate.of(2024, 12, 19),
            endDate = LocalDate.of(2024, 12, 19),
            type = InternalTimetableType.BACHELOR_SESSION_TIMETABLE
        )

        assertEquals(expected, actual)
    }
}