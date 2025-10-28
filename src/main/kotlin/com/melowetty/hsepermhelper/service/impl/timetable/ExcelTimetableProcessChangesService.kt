package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableStorage
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class ExcelTimetableProcessChangesService(
    private val storage: ExcelTimetableStorage,
    private val timetableNotificationService: TimetableNotificationService,
) {
    // TODO: сделать запуск задач по проверке изменений и отправке уведомлений

    fun processChangesByRunIds(prevRunId: String, currentRunId: String) {
        val oldTimetables = storage.getTimetablesInfoIdByRunId(prevRunId)
        val oldTimetablesIds = oldTimetables.map { it.id }.toSet()

        val currentTimetables = storage.getTimetablesInfoIdByRunId(currentRunId)
        val currentTimetablesIds = currentTimetables.map { it.id }.toSet()

        val groupedSimilarTimetables = (oldTimetables + currentTimetables).groupBy {
            Triple(it.start, it.end, it.type)
        }

        val addedOrDeletedTimetables = groupedSimilarTimetables.values.filter { it.size == 1 }.flatten()

        val addedTimetables = addedOrDeletedTimetables.filter { it.id !in oldTimetablesIds }
        val deletedTimetables = addedOrDeletedTimetables.filter { it.id in oldTimetablesIds }
        val existsTimetables = groupedSimilarTimetables.values.filter { it.size == 2 }.map { Pair(it[0], it[1]) }

        val notChangedTimetables = existsTimetables.filter {
            val (prev, current) = it
            prev.lessonsHash == current.lessonsHash
        }

        val changedTimetables = existsTimetables.filter {
            val (prev, current) = it
            prev.lessonsHash != current.lessonsHash
        }

        logger.info { "Added ${addedTimetables.size} timetables, deleted ${deletedTimetables.size} timetables," +
                " changed ${changedTimetables.size} timetables and not changed ${notChangedTimetables.size} timetables" }

        processTimetables(currentRunId, addedTimetables, changedTimetables, notChangedTimetables)

        storage.showTimetables(currentTimetablesIds.toList())

        if (notChangedTimetables.size != currentTimetables.size) {
            storage.deleteTimetablesByRunId(prevRunId)
        }
    }

    private fun processTimetables(
        currentRunId: String,
        added: List<InternalTimetableInfo>,
        changed: List<Pair<InternalTimetableInfo, InternalTimetableInfo>>,
        notChanged: List<Pair<InternalTimetableInfo, InternalTimetableInfo>>,
    ) {
        processNotChangedTimetables(currentRunId, notChanged)
        processAddedTimetables(added)

    }

    private fun processNotChangedTimetables(
        currentRunId: String,
        notChanged: List<Pair<InternalTimetableInfo, InternalTimetableInfo>>,
    ) {
        val oldTimetablesIds = notChanged.map {
            it.first.id
        }

        val newTimetablesIds = notChanged.map {
            it.second.id
        }

        storage.updateTimetablesRunId(oldTimetablesIds, currentRunId)
        storage.deleteTimetables(newTimetablesIds)
    }

    private fun processAddedTimetables(
        added: List<InternalTimetableInfo>,
    ) {
        storage.showTimetables(added.map { it.id })

        val mustBeNotified = added.filter { it.isParent }

        if (mustBeNotified.isEmpty()) {
            return
        }

        timetableNotificationService.notifyAboutAddedTimetables(mustBeNotified)
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}