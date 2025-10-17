package com.melowetty.hsepermhelper.timetable

import com.melowetty.hsepermhelper.timetable.model.InternalTimetableProcessorType

object TimetableInfoEncoder {
    fun encode(id: String, type: InternalTimetableProcessorType): String {
        return "${id}-${type.ordinal}"
    }

    fun decode(id: String): Pair<String, InternalTimetableProcessorType> {
        return id.split("-").let {
            if (it.size == 2) {
                Pair(it[0], InternalTimetableProcessorType.entries[it[1].toInt()])
            } else {
                throw IllegalArgumentException("Invalid timetable id: $id")
            }
        }
    }
}