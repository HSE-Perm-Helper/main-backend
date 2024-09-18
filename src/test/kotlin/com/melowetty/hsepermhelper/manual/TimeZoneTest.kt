package com.melowetty.hsepermhelper.manual

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.TimeZone

class TimeZoneTest {
    @Test
    fun `perm time zone get from string`() {
        val timeZone: TimeZone = TimeZone.getTimeZone("GMT+05:00")

        assertEquals(timeZone.rawOffset, 1000 * 60 * 60 * 5)
    }
}