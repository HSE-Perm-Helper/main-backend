package com.melowetty.hsepermhelper.util

import java.time.LocalTime

class DateUtils {
    companion object {
        const val DATE_PATTERN = "dd.MM.yyyy"
        const val DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss"
        fun parseTime(time: String): LocalTime {
            val split = time.split(":").map { it.toInt() }
            return LocalTime.of(split[0], split[1])
        }
    }
}