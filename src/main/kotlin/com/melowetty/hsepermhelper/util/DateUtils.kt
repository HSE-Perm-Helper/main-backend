package com.melowetty.hsepermhelper.util

import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.util.TimeZone

class DateUtils {
    companion object {
        const val DATE_PATTERN = "dd.MM.yyyy"

        const val DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss"

        const val PERM_TIME_ZONE_STR = "GMT+05:00"

        val PERM_TIME_ZONE: TimeZone = TimeZone.getTimeZone(PERM_TIME_ZONE_STR)

        val PERM_TIME_OFFSET: ZoneOffset = ZoneOffset.of(PERM_TIME_ZONE.toZoneId().normalized().id)

        fun parseTime(time: String): LocalTime {
            val split = time.split(":").map { it.toInt() }
            return LocalTime.of(split[0], split[1])
        }

        fun OffsetTime.asStr(): String {
            val hour = hour
            val minutes = minute
            val zeros = "0".repeat(2 - minutes.toString().length)

            return "${hour}:${zeros}${minutes}"
        }
    }
}