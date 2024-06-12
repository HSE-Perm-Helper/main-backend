package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.File
import com.melowetty.hsepermhelper.model.FilesChanging
import com.melowetty.hsepermhelper.repository.ScheduleFilesRepository
import com.melowetty.hsepermhelper.service.impl.ScheduleFilesServiceImpl
import com.melowetty.hsepermhelper.util.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher


@ExtendWith(MockitoExtension::class)
class ScheduleFilesServiceImplTest {
    @Mock
    private lateinit var scheduleFilesRepositoryMock: ScheduleFilesRepository
    @Mock
    private lateinit var eventPublisherMock: ApplicationEventPublisher
    @Mock
    private lateinit var filesCheckingChangesServiceMock: FilesCheckingChangesService

    @InjectMocks
    private lateinit var scheduleFilesService: ScheduleFilesServiceImpl

    @Test
    fun `test get empty list of schedule files`() {
        Mockito.`when`(scheduleFilesRepositoryMock.getScheduleFilesAsByteArray()).thenReturn(listOf())
        val expected = listOf<File>()
        val actual = scheduleFilesService.getScheduleFiles()
        assertEquals(expected, actual)
    }

    @Test
    fun `test get common list of schedule files`() {
        val firstFile = TestUtils.readFileAsInputStream("service/schedule-files/schedule_1.xls").readAllBytes()
        val secondFile = TestUtils.readFileAsInputStream("service/schedule-files/schedule_2.xls").readAllBytes()
        Mockito.`when`(scheduleFilesRepositoryMock.getScheduleFilesAsByteArray()).thenReturn(
            listOf(
                firstFile,
                secondFile,
            )
        )
        val expected = listOf(
            File(data = firstFile),
            File(data = secondFile),
        )
        val actual = scheduleFilesService.getScheduleFiles()
        assertEquals(expected, actual)
    }

    @Test
    fun `test fetch files when file do not contains changes`() {
        val file = TestUtils.readFileAsInputStream("service/schedule-files/schedule_1.xls").readAllBytes()
        Mockito.`when`(scheduleFilesRepositoryMock.getScheduleFilesAsByteArray()).thenReturn(listOf(file), listOf(file))

        Mockito.`when`(filesCheckingChangesServiceMock.getChanges(
            before = listOf(File(data = file)),
            after = listOf(File(data = file)),
        )).thenReturn(
            FilesChanging(
                withoutChanges = listOf(File(data = file)),
            )
        )

        scheduleFilesService.fetchScheduleFilesAndPublishEvents()

        Mockito.verify(scheduleFilesRepositoryMock, Mockito.times(1)).fetchScheduleFiles()

        Mockito.verify(filesCheckingChangesServiceMock, Mockito.times(1)).getChanges(
            before = listOf(File(data = file)),
            after = listOf(File(data = file))
        )

        Mockito.verify(eventPublisherMock, Mockito.never()).publishEvent(Mockito.any())
        Mockito.verify(scheduleFilesRepositoryMock, Mockito.times(2)).getScheduleFilesAsByteArray()
    }

    @Test
    fun `test fetch files when files is changed`() {
        val firstFile = TestUtils.readFileAsInputStream("service/schedule-files/schedule_1.xls").readAllBytes()
        val secondFile = TestUtils.readFileAsInputStream("service/schedule-files/schedule_2.xls").readAllBytes()
        Mockito.`when`(scheduleFilesRepositoryMock.getScheduleFilesAsByteArray()).thenReturn(listOf(firstFile), listOf(secondFile))

        Mockito.`when`(filesCheckingChangesServiceMock.getChanges(
            before = listOf(File(data = firstFile)),
            after = listOf(File(data = secondFile)),
        )).thenReturn(
            FilesChanging(
                addedOrChanged = listOf(File(data = secondFile)),
                deleted = listOf(File(data = firstFile))
            )
        )

        scheduleFilesService.fetchScheduleFilesAndPublishEvents()

        Mockito.verify(scheduleFilesRepositoryMock, Mockito.times(1)).fetchScheduleFiles()

        Mockito.verify(filesCheckingChangesServiceMock, Mockito.times(1)).getChanges(
            before = listOf(File(data = firstFile)),
            after = listOf(File(data = secondFile))
        )

        Mockito.verify(eventPublisherMock, Mockito.times(1)).publishEvent(
            FilesChanging(
                addedOrChanged = listOf(File(data = secondFile)),
                deleted = listOf(File(data = firstFile))
            )
        )
        Mockito.verify(scheduleFilesRepositoryMock, Mockito.times(2)).getScheduleFilesAsByteArray()
    }
}