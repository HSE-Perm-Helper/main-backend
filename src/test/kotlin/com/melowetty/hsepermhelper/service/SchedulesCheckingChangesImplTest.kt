package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.excel.ExcelScheduleDifference
import com.melowetty.hsepermhelper.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.model.event.ExcelSchedulesChanging
import com.melowetty.hsepermhelper.service.impl.SchedulesCheckingChangesServiceImpl
import com.melowetty.hsepermhelper.util.TestUtils.Companion.getSchedule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate


@SpringBootTest(classes = [SchedulesCheckingChangesServiceImpl::class])
class SchedulesCheckingChangesImplTest {
    @Autowired
    lateinit var schedulesCheckingChangesService: SchedulesCheckingChangesService

    @Test
    fun `get changes when do not exists any changes`() {
        val before = listOf(getSchedule(), getSchedule())
        val after = before.toList()
        val actual = schedulesCheckingChangesService.getChanges(before, after)
        val expected = ExcelSchedulesChanging()
        assertEquals(expected, actual)
    }

    @Test
    fun `get changes when schedules is deleted`() {
        val before = listOf(getSchedule(), getSchedule())
        val after = listOf<Schedule>()
        val actual = schedulesCheckingChangesService.getChanges(before, after)
        val expected = ExcelSchedulesChanging(deleted = before)
        assertEquals(expected, actual)
    }

    @Test
    fun `get changes when schedules is added`() {
        val before = listOf<Schedule>()
        val after = listOf(getSchedule(), getSchedule())
        val actual = schedulesCheckingChangesService.getChanges(before, after)
        val expected = ExcelSchedulesChanging(added = after)
        assertEquals(expected, actual)
    }

    @Test
    fun `get changes when schedules is changed`() {
        val before = getSchedule()
        val after = before.copy(
            lessons = listOf(before.lessons.first())
        )
        val actual = schedulesCheckingChangesService.getChanges(listOf(before), listOf(after))
        val expected = ExcelSchedulesChanging(
            changed = listOf(ExcelScheduleDifference(before = before, after = after))
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `test normal situation after changing schedule on site`() {
        val beforeFirst = getSchedule()
        val beforeSecond = getSchedule().copy(scheduleType = ScheduleType.SESSION_SCHEDULE)
        val beforeThird = getSchedule().copy(scheduleType = ScheduleType.QUARTER_SCHEDULE)
        val before = listOf(
            beforeFirst,
            beforeSecond,
            beforeThird
        )

        val afterFirst = beforeFirst.copy()
        val afterSecond = beforeSecond.copy(
            lessons = beforeSecond.lessons.subList(0, 3)
        )
        val afterThird = getSchedule().copy(end = LocalDate.of(2024, 10, 20))
        val after = listOf(afterFirst, afterSecond, afterThird)

        val expected = ExcelSchedulesChanging(
            added = listOf(afterThird),
            deleted = listOf(beforeThird),
            changed = listOf(ExcelScheduleDifference(before = beforeSecond, after = afterSecond))
        )

        val actual = schedulesCheckingChangesService.getChanges(before, after)

        assertEquals(expected, actual)
    }

}