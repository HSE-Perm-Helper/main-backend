package com.melowetty.hsepermhelper.timetable

import com.melowetty.hsepermhelper.timetable.model.InternalTimetableSource

object TimetableInfoEncoder {
    private const val DELIMITER = "z"

    fun encode(id: String, type: InternalTimetableSource): String {
        return "${id}$DELIMITER${type.ordinal}"
    }

    fun decode(id: String): Pair<String, InternalTimetableSource> {
        return id.split(DELIMITER).let {
            val processor = it.takeLast(1).first().toIntOrNull()
                ?: throw IllegalArgumentException("Invalid timetable id: $id")

            val decodedId = it.dropLast(1).joinToString(DELIMITER)

            if (processor !in InternalTimetableSource.entries.indices) {
                throw IllegalArgumentException("Invalid timetable id: $id")
            }

            Pair(decodedId, InternalTimetableSource.entries[processor])
        }
    }
}