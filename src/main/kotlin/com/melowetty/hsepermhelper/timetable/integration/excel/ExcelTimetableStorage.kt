package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component

@Component
class ExcelTimetableStorage {
    private val storage = mutableMapOf<String, ExcelTimetable>()

    fun getParentTimetables(): List<InternalTimetableInfo> {
        return listOf()
    }

    fun getTimetableFilteredByGroup(id: String, group: String): InternalTimetable {
        val timetable = storage[id]
            ?: throw IllegalArgumentException("Timetable with id $id not found")

        timetable.
    }

    fun hideTimetablesOlderThan(timestamp: Long) {
        TODO()
    }

    fun showTimetables(ids: List<String>) {
        TODO()
    }

    fun deleteTimetablesOlderThan(timestamp: Long) {
        TODO()
    }

    fun saveTimetable(timetable: ExcelTimetable): ExcelTimetable {
        val id = generateId()
        timetable.id = id
        storage[id] = timetable

        return timetable
    }

    private fun generateId(): String {
        return RandomStringUtils.randomAlphanumeric(10)
    }
}