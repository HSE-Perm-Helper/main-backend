package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.excel.impl.HseTimetableExcelParserImpl
import com.melowetty.hsepermhelper.excel.model.ParsedScheduleInfo
import com.melowetty.hsepermhelper.service.NotificationService
import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.shared.BachelorTimetableSheetExcelParser
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
class HseTimetableExcelParserTest {
    @InjectMocks
    private lateinit var parser: HseTimetableExcelParserImpl

    @Mock
    private lateinit var sheetParser: BachelorTimetableSheetExcelParser

    @Mock
    private lateinit var typeChecker: HseTimetableScheduleTypeChecker

    @Mock
    private lateinit var notificationService: NotificationService

    @Test
    fun `get basic schedule info`() {
        val header = "на 1 неделю (с 01.11.2024 по 19.12.2024)"

        Mockito.`when`(typeChecker.getScheduleType(any())).thenReturn(ScheduleType.WEEK_SCHEDULE)
        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = 1,
            startDate = LocalDate.of(2024, 11, 1),
            endDate = LocalDate.of(2024, 12, 19),
            type = ScheduleType.WEEK_SCHEDULE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get session schedule info`() {
        val header = "на сессию (с 01.11.2024 по 19.12.2024)"

        Mockito.`when`(typeChecker.getScheduleType(any())).thenReturn(ScheduleType.SESSION_SCHEDULE)
        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = null,
            startDate = LocalDate.of(2024, 11, 1),
            endDate = LocalDate.of(2024, 12, 19),
            type = ScheduleType.SESSION_SCHEDULE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get quarter schedule info`() {
        val header = "на 2 модуль (с 01.11.2024 по 19.12.2024)"

        Mockito.`when`(typeChecker.getScheduleType(any())).thenReturn(ScheduleType.QUARTER_SCHEDULE)
        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = 2,
            startDate = LocalDate.of(2024, 11, 1),
            endDate = LocalDate.of(2024, 12, 19),
            type = ScheduleType.QUARTER_SCHEDULE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get schedule info when wrong format for dates`() {
        val header = "на 2 модуль (с 01.11.20.24 по 19.12.2024)"

        Mockito.`when`(typeChecker.getScheduleType(any())).thenReturn(ScheduleType.QUARTER_SCHEDULE)
        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = 2,
            startDate = LocalDate.of(2024, 11, 1),
            endDate = LocalDate.of(2024, 12, 19),
            type = ScheduleType.QUARTER_SCHEDULE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get schedule info when get week number and one day`() {
        val header = "на 6 неделю (19.12.2024)"

        Mockito.`when`(typeChecker.getScheduleType(any())).thenReturn(ScheduleType.WEEK_SCHEDULE)
        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = 6,
            startDate = LocalDate.of(2024, 12, 19),
            endDate = LocalDate.of(2024, 12, 19),
            type = ScheduleType.WEEK_SCHEDULE
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `get schedule info when no number week and one day`() {
        val header = "на сессию (19.12.2024)"

        Mockito.`when`(typeChecker.getScheduleType(any())).thenReturn(ScheduleType.SESSION_SCHEDULE)
        val actual = parser.parseScheduleInfo(header)
        val expected = ParsedScheduleInfo(
            number = null,
            startDate = LocalDate.of(2024, 12, 19),
            endDate = LocalDate.of(2024, 12, 19),
            type = ScheduleType.SESSION_SCHEDULE
        )

        assertEquals(expected, actual)
    }
}