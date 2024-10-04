package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.File
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


@ExtendWith(MockitoExtension::class)
class ScheduleFilesServiceImplTest {
    @Mock
    private lateinit var scheduleFilesRepositoryMock: ScheduleFilesRepository

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
}