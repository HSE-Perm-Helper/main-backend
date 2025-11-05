package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.persistence.storage.ExcelTimetableStorage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class ExcelTimetableNormalizerService(
    private val excelTimetableStorage: ExcelTimetableStorage,
) {
    fun normalizeTimetables(ids: List<String>) {
        logger.info { "Normalizing timetables with ids: ${ids.joinToString(", ")}" }

        val info = excelTimetableStorage.getTimetablesInfo(ids)

        val timetablesToMerge = info
            .filter { it.type.shouldBeMerged }
            .groupBy { it.type }
            .map {
                it.value
            }

        timetablesToMerge.forEach { timetables ->
            val ids = timetables.map { it.id }
            excelTimetableStorage.mergeTimetables(ids)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}