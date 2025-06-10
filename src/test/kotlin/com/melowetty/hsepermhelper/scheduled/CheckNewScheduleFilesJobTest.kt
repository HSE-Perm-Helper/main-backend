package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.domain.model.file.FilesChanging
import com.melowetty.hsepermhelper.service.FilesCheckingChangesService
import com.melowetty.hsepermhelper.service.ScheduleFilesService
import com.melowetty.hsepermhelper.util.TestUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockitoExtension::class)
class CheckNewScheduleFilesJobTest {
    @Mock
    private lateinit var eventPublisherMock: ApplicationEventPublisher

    @Mock
    private lateinit var filesCheckingChangesServiceMock: FilesCheckingChangesService

    @Mock
    private lateinit var scheduleFilesServiceMock: ScheduleFilesService

    @InjectMocks
    private lateinit var checkNewScheduleFilesJob: CheckNewScheduleFilesJob

    @Test
    fun `test fetch files when file do not contains changes`() {
        val file = File(
            data = TestUtils.readFileAsInputStream("service/schedule-files/schedule_1.xls").readAllBytes(),
            name = "schedule_1.xls"
        )
        Mockito.`when`(scheduleFilesServiceMock.getScheduleFiles()).thenReturn(listOf(file), listOf(file))

        Mockito.`when`(
            filesCheckingChangesServiceMock.getChanges(
                before = listOf(file),
                after = listOf(file),
            )
        ).thenReturn(
            FilesChanging(
                withoutChanges = listOf(file),
            )
        )

        checkNewScheduleFilesJob.fetchScheduleFilesAndPublishEvents()

        Mockito.verify(scheduleFilesServiceMock, Mockito.times(1)).fetchScheduleFiles()

        Mockito.verify(filesCheckingChangesServiceMock, Mockito.times(1)).getChanges(
            before = listOf(file),
            after = listOf(file)
        )

        Mockito.verify(eventPublisherMock, Mockito.never()).publishEvent(Mockito.any())
        Mockito.verify(scheduleFilesServiceMock, Mockito.times(2)).getScheduleFiles()
    }

    @Test
    fun `test fetch files when files is changed`() {
        val firstFile = File(
            data = TestUtils.readFileAsInputStream("service/schedule-files/schedule_1.xls").readAllBytes(),
            name = "schedule_1.xls"
        )
        val secondFile = File(
            data = TestUtils.readFileAsInputStream("service/schedule-files/schedule_2.xls").readAllBytes(),
            name = "schedule_2.xls"
        )
        Mockito.`when`(scheduleFilesServiceMock.getScheduleFiles()).thenReturn(listOf(firstFile), listOf(secondFile))

        Mockito.`when`(
            filesCheckingChangesServiceMock.getChanges(
                before = listOf(firstFile),
                after = listOf(secondFile),
            )
        ).thenReturn(
            FilesChanging(
                addedOrChanged = listOf(secondFile),
                deleted = listOf(firstFile)
            )
        )

        checkNewScheduleFilesJob.fetchScheduleFilesAndPublishEvents()

        Mockito.verify(scheduleFilesServiceMock, Mockito.times(1)).fetchScheduleFiles()

        Mockito.verify(filesCheckingChangesServiceMock, Mockito.times(1)).getChanges(
            before = listOf(firstFile),
            after = listOf(secondFile)
        )

        Mockito.verify(eventPublisherMock, Mockito.times(1)).publishEvent(
            FilesChanging(
                addedOrChanged = listOf(secondFile),
                deleted = listOf(firstFile)
            )
        )
        Mockito.verify(scheduleFilesServiceMock, Mockito.times(2)).getScheduleFiles()
    }
}