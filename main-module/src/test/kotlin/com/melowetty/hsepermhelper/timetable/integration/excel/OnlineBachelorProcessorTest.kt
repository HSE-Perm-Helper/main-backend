package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.timetable.integration.excel.bachelor.online.OnlineBachelorTimetableProcessor
import com.melowetty.hsepermhelper.domain.model.timetable.EducationType
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

        assertThat(timetables).hasSize(1)

        val timetable = timetables.first()
        assertThat(timetable.number).isEqualTo(1)
        assertThat(timetable.educationType).isEqualTo(EducationType.BACHELOR_ONLINE)
        assertThat(timetable.lessons).isNotEmpty()

        workbook.close()
    }

    @Test
    fun `should identify online schedule by filename`() {
        assertThat(processor.isParseable("ОП_УБ_1_курс.xls")).isTrue()
        assertThat(processor.isParseable("Расписание_ОП.xlsx")).isTrue()
        assertThat(processor.isParseable("regular_schedule.xls")).isFalse()
    }
}
