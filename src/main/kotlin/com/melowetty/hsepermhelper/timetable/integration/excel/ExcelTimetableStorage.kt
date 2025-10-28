package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.context.JobRunContextHolder
import com.melowetty.hsepermhelper.exception.TimetableByIdNotFoundException
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toInfo
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ExcelTimetableStorage {
    private val storage = mutableMapOf<String, ExcelTimetable>()
    private val idsByRunId = mutableMapOf<String, List<String>>()
    private val showedTimetables = mutableSetOf<String>()

    fun getParentTimetables(): List<InternalTimetableInfo> {
        return storage.values
            .filter { it.isParent }
            .filter { it.id() in showedTimetables }
            .map { it.toInfo() }
    }

    fun getTimetableFilteredByGroup(id: String, group: String): ExcelTimetable {
        val timetable = storage[id]
            ?: throw TimetableByIdNotFoundException(id)

        return timetable.copy(
            lessons = timetable.lessons.filter { it.group == group }
        )
    }

    fun getTimetables(ids: List<String>): List<ExcelTimetable> {
        return ids.mapNotNull { id -> storage[id] }
    }

    fun getTimetable(id: String): ExcelTimetable {
        return storage[id]
            ?: throw TimetableByIdNotFoundException(id)
    }

    fun getTimetableInfo(id: String): InternalTimetableInfo {
        return storage[id]?.toInfo()
            ?: throw TimetableByIdNotFoundException(id)
    }

    fun getTimetablesGroups(ids: List<String>): List<String> {
        return ids.mapNotNull { id -> storage[id]?.lessons }.flatten().map { it.group }.distinct()
    }

    fun getTimetablesInfo(ids: List<String>): List<InternalTimetableInfo> {
        return ids.mapNotNull { id -> storage[id]?.toInfo() }
    }

    fun getTimetablesInfoIdByRunId(runId: String): List<InternalTimetableInfo> {
        val ids = idsByRunId[runId] ?: emptyList()
        return ids.mapNotNull { id -> storage[id]?.toInfo() }
    }

    fun showTimetables(ids: List<String>) {
        ids.forEach { id -> showedTimetables.add(id) }
    }

    fun updateTimetable(id: String, timetable: ExcelTimetable) {
        val updatedTimetable: ExcelTimetable = timetable.updateTimestamp()

        storage[id] = updatedTimetable
    }

    fun updateTimetablesRunId(ids: List<String>, runId: String) {
        val oldRunIds = idsByRunId.keys.toSet()
        oldRunIds.forEach {
            if (ids.containsAll(idsByRunId[it] ?: emptyList())) {
                idsByRunId.remove(it)
            }
        }

        ids.forEach {
            idsByRunId[runId] = idsByRunId.getOrDefault(runId, emptyList()) + it
        }
    }

    fun deleteTimetables(ids: List<String>) {
        ids.forEach {
            storage.remove(it)
            showedTimetables.remove(it)
        }
    }

    fun deleteTimetable(id: String) {
        storage.remove(id)
        showedTimetables.remove(id)
    }

    @Transactional
    fun mergeTimetables(timetablesIds: List<String>) {
        val timetables = timetablesIds.map { id -> getTimetable(id) }
        val targetTimetable = timetables.sortedBy { it.start }.reduce { acc, timetable ->
            acc.copy(
                lessons = acc.lessons + timetable.lessons,
                end = maxOf(acc.end, timetable.end)
            )
        }.let {
            it.copy(
                lessons = it.lessons.sortedBy { it.time }
            )
        }

        updateTimetable(targetTimetable.id(), targetTimetable)

        timetablesIds.filter { it != targetTimetable.id() }.forEach { deleteTimetable(it) }
    }

    fun deleteTimetablesByRunId(runId: String) {
        val ids = idsByRunId[runId]
            ?: run {
                logger.warn { "Timetable ids by run $runId not found" }
                return
            }

        ids.forEach {
            storage.remove(it)
        }

        idsByRunId.remove(runId)
    }

    fun saveTimetableAsHidden(timetable: ExcelTimetable): ExcelTimetable {
        val id = generateId()
        timetable.id = id

        val saveTimetable = timetable.updateTimestamp()
        storage[id] = saveTimetable

        val runId = JobRunContextHolder.get()?.id
            ?: run {
                logger.warn { "Run job context is not defined" }
                return timetable
            }

        idsByRunId[runId] = idsByRunId.getOrDefault(runId, emptyList()) + id

        return timetable
    }

    private fun generateId(): String {
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH).lowercase()
    }

    companion object {
        private const val ID_LENGTH = 6

        private val logger = KotlinLogging.logger {  }
    }
}