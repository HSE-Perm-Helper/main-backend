package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.models.File
import com.melowetty.hsepermhelper.models.FilesChanging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FilesCheckingChangesServiceTest {
    @Autowired
    lateinit var filesCheckingChangesService: FilesCheckingChangesService

    private fun readFile(file: String): File {
        return File(inputStream = this.javaClass.getResourceAsStream(file)!!)
    }

    @Test
    fun `test when no files before and no files after`() {
        val expected = FilesChanging(
            addedOrChanged = listOf(),
            withoutChanges = listOf(),
            deleted = listOf()
        )
        val actual = filesCheckingChangesService.getChanges(before = listOf(), after = listOf())
        assertEquals(expected, actual)
    }

    @Test
    fun `test when before was two files and after one of it was changed`() {
        val firstScheduleBefore = readFile("schedule_1_before.xls")
        val secondScheduleBefore = readFile("schedule_2_before.xls")
        val firstScheduleAfter = readFile("schedule_1_after_no_changes.xls")
        val secondScheduleAfter = readFile("schedule_2_after_changed.xls")
        val expected = FilesChanging(
            addedOrChanged = listOf(secondScheduleAfter),
            withoutChanges = listOf(firstScheduleAfter),
            deleted = listOf(secondScheduleBefore)
        )
        val actual = filesCheckingChangesService.getChanges(
            before = listOf(firstScheduleBefore, secondScheduleBefore),
            after = listOf(firstScheduleAfter, secondScheduleAfter)
        )
        assertEquals(expected, actual)
    }
}