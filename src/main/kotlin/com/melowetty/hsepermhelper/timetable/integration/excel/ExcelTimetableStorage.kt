package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toInfo
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime

@Component
class ExcelTimetableStorage {
    private val storage = mutableMapOf<String, ExcelTimetable>()
    private val showedTimetables = mutableSetOf<String>()

    fun getParentTimetables(): List<InternalTimetableInfo> {
        return storage.values.map { it.toInfo() }
    }

    fun getTimetableFilteredByGroup(id: String, group: String): ExcelTimetable {
        val timetable = storage[id]
            ?: throw IllegalArgumentException("Timetable with id $id not found")

        return timetable.copy(
            lessons = timetable.lessons.filter { it.group == group }
        )
    }

    fun getTimetable(id: String): ExcelTimetable {
        return storage[id]
            ?: throw IllegalArgumentException("Timetable with id $id not found")
    }

    fun getTimetableInfo(id: String): InternalTimetableInfo {
        return storage[id]?.toInfo()
            ?: throw IllegalArgumentException("Timetable with id $id not found")
    }

    fun showTimetables(ids: List<String>) {
        ids.forEach { id -> showedTimetables.add(id) }
    }

    fun updateTimetable(id: String, timetable: ExcelTimetable) {
        val updatedTimetable: ExcelTimetable = timetable.updateTimestamp()

        storage[id] = timetable
    }

    fun saveTimetableAsHidden(timetable: ExcelTimetable): ExcelTimetable {
        val id = generateId()
        timetable.id = id

        val saveTimetable = timetable.updateTimestamp()
        storage[id] = saveTimetable

        return timetable
    }

    private fun generateId(): String {
        return RandomStringUtils.randomAlphanumeric(10)
    }
}