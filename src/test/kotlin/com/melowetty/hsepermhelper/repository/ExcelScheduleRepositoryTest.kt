package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.domain.model.event.ExcelSchedulesChanging
import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.domain.model.file.FilesChanging
import com.melowetty.hsepermhelper.excel.HseTimetableExcelParser
import com.melowetty.hsepermhelper.excel.model.ExcelScheduleDifference
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import com.melowetty.hsepermhelper.service.SchedulesCheckingChangesService
import com.melowetty.hsepermhelper.util.MockitoHelper
import com.melowetty.hsepermhelper.util.TestUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockitoExtension::class)
class ExcelScheduleRepositoryTest {
    @InjectMocks
    private lateinit var excelScheduleRepository: ExcelScheduleRepository

    @Mock
    private lateinit var eventPublisherMock: ApplicationEventPublisher

    @Mock
    private lateinit var scheduleFilesServiceMock: ScheduleFilesService

    @Mock
    private lateinit var timetableExcelParser: HseTimetableExcelParser

    @Mock
    private lateinit var schedulesCheckingChangesServiceMock: SchedulesCheckingChangesService

    @Test
    fun `test handle event when schedule is changed`() {
        val firstFile = File(
            TestUtils.readFileAsInputStream("repository/schedule-repository/schedule_1.xls").readAllBytes(),
            name = "schedule_1.xls"
        )
        val secondFile = File(
            TestUtils.readFileAsInputStream("repository/schedule-repository/schedule_2.xls").readAllBytes(),
            name = "schedule_2.xls"
        )

        val firstSchedule = TestUtils.getSchedule()
        val secondSchedule = firstSchedule.copy(
            lessons = firstSchedule.lessons.subList(0, 3)
        )

        Mockito.`when`(scheduleFilesServiceMock.getScheduleFiles()).thenReturn(listOf(firstFile), listOf(secondFile))

        Mockito.`when`(
            schedulesCheckingChangesServiceMock.getChanges(
                before = MockitoHelper.anyObject(),
                after = MockitoHelper.anyObject(),
            )
        ).thenReturn(
            ExcelSchedulesChanging(
                changed = listOf(
                    ExcelScheduleDifference(
                        before = firstSchedule,
                        after = secondSchedule,
                    )
                )
            )
        )

        excelScheduleRepository.handleScheduleFilesUpdate(FilesChanging(addedOrChanged = listOf(firstFile, secondFile)))

        Mockito.verify(scheduleFilesServiceMock, Mockito.times(1)).getScheduleFiles()

        Mockito.verify(schedulesCheckingChangesServiceMock, Mockito.times(1)).getChanges(
            before = MockitoHelper.anyObject(),
            after = MockitoHelper.anyObject(),
        )

        Mockito.verify(eventPublisherMock, Mockito.times(1)).publishEvent(
            ExcelSchedulesChanging(
                changed = listOf(
                    ExcelScheduleDifference(
                        before = firstSchedule,
                        after = secondSchedule,
                    )
                )
            )
        )
    }

    @Test
    fun `test handle event when schedule is not changed`() {
        val firstFile = File(
            data = TestUtils.readFileAsInputStream("repository/schedule-repository/schedule_1.xls").readAllBytes(),
            name = "schedule_1.xls"
        )
        val secondFile = File(
            data = TestUtils.readFileAsInputStream("repository/schedule-repository/schedule_1.xls").readAllBytes(),
            name = "schedule_1.xls"
        )

        Mockito.`when`(scheduleFilesServiceMock.getScheduleFiles()).thenReturn(listOf(firstFile), listOf(secondFile))

        Mockito.`when`(
            schedulesCheckingChangesServiceMock.getChanges(
                before = MockitoHelper.anyObject(),
                after = MockitoHelper.anyObject(),
            )
        ).thenReturn(ExcelSchedulesChanging())

        excelScheduleRepository.handleScheduleFilesUpdate(FilesChanging(withoutChanges = listOf(firstFile, secondFile)))

        Mockito.verify(scheduleFilesServiceMock, Mockito.times(1)).getScheduleFiles()

        Mockito.verify(schedulesCheckingChangesServiceMock, Mockito.times(1)).getChanges(
            before = MockitoHelper.anyObject(),
            after = MockitoHelper.anyObject(),
        )

        Mockito.verify(eventPublisherMock, Mockito.never()).publishEvent(
            Mockito.any()
        )
    }
}