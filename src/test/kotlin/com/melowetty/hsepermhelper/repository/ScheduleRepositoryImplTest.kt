package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.excel.HseTimetableExcelParser
import com.melowetty.hsepermhelper.model.File
import com.melowetty.hsepermhelper.model.FilesChanging
import com.melowetty.hsepermhelper.model.ScheduleDifference
import com.melowetty.hsepermhelper.model.SchedulesChanging
import com.melowetty.hsepermhelper.repository.impl.ScheduleRepositoryImpl
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
class ScheduleRepositoryImplTest {
    @InjectMocks
    private lateinit var scheduleRepository: ScheduleRepositoryImpl
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
        val firstFile = File(TestUtils.readFileAsInputStream("repository/schedule-repository/schedule_1.xls").readAllBytes())
        val secondFile = File(TestUtils.readFileAsInputStream("repository/schedule-repository/schedule_2.xls").readAllBytes())

        val firstSchedule = TestUtils.getSchedule()
        val secondSchedule = firstSchedule.copy(
            lessons = firstSchedule.lessons.subList(0, 3)
        )

        Mockito.`when`(scheduleFilesServiceMock.getScheduleFiles()).thenReturn(listOf(firstFile), listOf(secondFile))

        Mockito.`when`(schedulesCheckingChangesServiceMock.getChanges(
            before = MockitoHelper.anyObject(),
            after = MockitoHelper.anyObject(),
        )).thenReturn(SchedulesChanging(
            changed = listOf(ScheduleDifference(
                before = firstSchedule,
                after = secondSchedule,
            ))
        ))

        scheduleRepository.handleScheduleFilesUpdate(FilesChanging(addedOrChanged = listOf(firstFile, secondFile)))

        Mockito.verify(scheduleFilesServiceMock, Mockito.times(1)).getScheduleFiles()

        Mockito.verify(schedulesCheckingChangesServiceMock, Mockito.times(1)).getChanges(
            before = MockitoHelper.anyObject(),
            after = MockitoHelper.anyObject(),
        )

        Mockito.verify(eventPublisherMock, Mockito.times(1)).publishEvent(
            SchedulesChanging(
                changed = listOf(ScheduleDifference(
                    before = firstSchedule,
                    after = secondSchedule,
                ))
            )
        )
    }

    @Test
    fun `test handle event when schedule is not changed`() {
        val firstFile = File(TestUtils.readFileAsInputStream("repository/schedule-repository/schedule_1.xls").readAllBytes())
        val secondFile = File(TestUtils.readFileAsInputStream("repository/schedule-repository/schedule_1.xls").readAllBytes())

        Mockito.`when`(scheduleFilesServiceMock.getScheduleFiles()).thenReturn(listOf(firstFile), listOf(secondFile))

        Mockito.`when`(schedulesCheckingChangesServiceMock.getChanges(
            before = MockitoHelper.anyObject(),
            after = MockitoHelper.anyObject(),
        )).thenReturn(SchedulesChanging())

        scheduleRepository.handleScheduleFilesUpdate(FilesChanging(withoutChanges = listOf(firstFile, secondFile)))

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