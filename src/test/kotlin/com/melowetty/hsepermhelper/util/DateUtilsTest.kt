package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.util.DateUtils.Companion.asStr
import java.time.LocalTime
import java.time.OffsetTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DateUtilsTest {
    @Test
    fun `offset time to str test when minute length is 1`() {
        val time = OffsetTime.of(
            5, 5, 0, 0, DateUtils.PERM_TIME_OFFSET
        )

        val expected = "5:05"
        val actual = time.asStr()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `offset time to str test when minute length is 2`() {
        val time = OffsetTime.of(
            5, 10, 0, 0, DateUtils.PERM_TIME_OFFSET
        )

        val expected = "5:10"
        val actual = time.asStr()

        Assertions.assertEquals(expected, actual)
    }
}