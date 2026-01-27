package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.util.DateUtils.Companion.asStr
import com.melowetty.hsepermhelper.util.DateUtils.Companion.fromGmtToPermTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DateUtilsTest {
    @Test
    fun `offset time to str test when minute length is 1`() {
        val time = LocalTime.of(
            5, 5
        ).atDate(LocalDate.MIN)

        val expected = "10:05"
        val actual = time.fromGmtToPermTime().asStr()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `offset time to str test when minute length is 2`() {
        val time = LocalTime.of(
            5, 10
        ).atDate(LocalDate.MIN)

        val expected = "10:10"
        val actual = time.fromGmtToPermTime().asStr()

        Assertions.assertEquals(expected, actual)
    }
}