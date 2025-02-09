package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.file.FilesChanging
import com.melowetty.hsepermhelper.service.impl.FilesCheckingChangesByHashcodeService
import com.melowetty.hsepermhelper.util.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [FilesCheckingChangesByHashcodeService::class])
class FilesCheckingChangesServiceTest {
    @Autowired
    lateinit var filesCheckingChangesService: FilesCheckingChangesService

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
        val firstScheduleBefore = TestUtils.readFile("service/files-checking-changes/schedule_1_before.xls")
        val secondScheduleBefore = TestUtils.readFile("service/files-checking-changes/schedule_2_before.xls")
        val firstScheduleAfter = TestUtils.readFile("service/files-checking-changes/schedule_1_after_no_changes.xls")
        val secondScheduleAfter = TestUtils.readFile("service/files-checking-changes/schedule_2_after_changed.xls")
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