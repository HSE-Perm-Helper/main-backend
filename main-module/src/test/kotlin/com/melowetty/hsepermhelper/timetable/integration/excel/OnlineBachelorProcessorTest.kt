package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.online.OnlineBachelorTimetableProcessor
import com.melowetty.hsepermhelper.domain.model.timetable.EducationType
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableSource
import com.melowetty.hsepermhelper.util.TestUtils
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OnlineBachelorProcessorTest {

    private val processor = OnlineBachelorTimetableProcessor()

    @Test
    fun `should parse online bachelor schedule successfully`() {
        val workbook = WorkbookFactory.create(TestUtils.readFileAsInputStream("service/schedule-files/schedule_3.xls"))

        val timetables = processor.process(workbook)

        assertThat(timetables).hasSize(12)
        assertThat(timetables).allMatch { it.educationType == EducationType.BACHELOR_ONLINE }
        assertThat(timetables).anyMatch { it.lessons.isNotEmpty() }

        workbook.close()
    }

    @Test
    fun `should identify online schedule by filename`() {
        assertThat(processor.isParseable("ОП_УБ_1_курс.xls")).isTrue()
        assertThat(processor.isParseable("Расписание_ОП.xlsx")).isTrue()
        assertThat(processor.isParseable("regular_schedule.xls")).isFalse()
    }

    @Test
    fun `should set isParent true and source EXCEL`() {
        val workbook = WorkbookFactory.create(TestUtils.readFileAsInputStream("service/schedule-files/schedule_3.xls"))

        val timetable = processor.process(workbook).first()

        assertThat(timetable.isParent).isTrue()
        assertThat(timetable.source).isEqualTo(InternalTimetableSource.EXCEL)

        workbook.close()
    }

    @Test
    fun `should parse start and end dates for each timetable`() {
        val workbook = WorkbookFactory.create(TestUtils.readFileAsInputStream("service/schedule-files/schedule_3.xls"))

        val timetables = processor.process(workbook)

        assertThat(timetables).allMatch { it.start != null && it.end != null }
        assertThat(timetables).allMatch { !it.start.isAfter(it.end) }

        workbook.close()
    }

    @Test
    fun `should have unique date ranges for each timetable`() {
        val workbook = WorkbookFactory.create(TestUtils.readFileAsInputStream("service/schedule-files/schedule_3.xls"))

        val timetables = processor.process(workbook)
        val startDates = timetables.map { it.start }

        assertThat(startDates).doesNotHaveDuplicates()

        workbook.close()
    }

    @Test
    fun `should have timetables sorted chronologically`() {
        val workbook = WorkbookFactory.create(TestUtils.readFileAsInputStream("service/schedule-files/schedule_3.xls"))

        val timetables = processor.process(workbook)

        for (i in 1 until timetables.size) {
            assertThat(timetables[i].start).isAfter(timetables[i - 1].start)
        }

        workbook.close()
    }
}
