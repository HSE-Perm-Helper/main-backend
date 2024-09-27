package com.melowetty.hsepermhelper.util

import java.time.LocalTime
import java.util.TimeZone

class DateUtils {
    companion object {
        const val DATE_PATTERN = "dd.MM.yyyy"
        const val DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss"
        val PERM_TIME_ZONE = TimeZone.getTimeZone("GMT+05:00")
        const val PERM_TIME_ZONE_STR = "GMT+05:00"
        fun parseTime(time: String): LocalTime {
            val split = time.split(":").map { it.toInt() }
            return LocalTime.of(split[0], split[1])
        }
    }
}