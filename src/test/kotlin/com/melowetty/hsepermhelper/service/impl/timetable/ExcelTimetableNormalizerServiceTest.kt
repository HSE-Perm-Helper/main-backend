package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableStorage
import com.melowetty.hsepermhelper.timetable.model.EducationType
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableSource
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class ExcelTimetableNormalizerServiceTest {
    @InjectMockKs
    private lateinit var excelTimetableNormalizerService: ExcelTimetableNormalizerService

    @MockK
    private lateinit var excelStorageService: ExcelTimetableStorage

    @Test
    fun `normalize timetable when have two session timetables, should be merge in one`() {
        val timetables = listOf(
            getTimetable("1", InternalTimetableType.BACHELOR_WEEK_TIMETABLE),
            getTimetable("2", InternalTimetableType.BACHELOR_SESSION_TIMETABLE),
            getTimetable("3", InternalTimetableType.BACHELOR_QUARTER_TIMETABLE),
            getTimetable("4", InternalTimetableType.BACHELOR_SESSION_TIMETABLE),
        )

        every { excelStorageService.getTimetablesInfo(timetables.map { it.id }) } returns timetables
        every { excelStorageService.mergeTimetables(any()) } just Runs

        excelTimetableNormalizerService.normalizeTimetables(timetables.map { it.id })

        verify(exactly = 1) {
            excelStorageService.mergeTimetables(
                listOf("2", "4")
            )
        }

    }

    private fun getTimetable(id: String, type: InternalTimetableType): InternalTimetableInfo {
        return InternalTimetableInfo(
            id = id,
            number = null,
            type = type,
            start = LocalDate.now(),
            end = LocalDate.now(),
            educationType = EducationType.BACHELOR_OFFLINE,
            isParent = true,
            lessonsHash = 123,
            source = InternalTimetableSource.EXCEL,
        )
    }
}