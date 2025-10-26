package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.context.JobRunContextHolder
import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableAdapter
import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableStorage
import com.melowetty.hsepermhelper.timetable.model.ExcelFileMetadata
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class ExcelTimetableProcessService(
    private val excelTimetableAdapter: ExcelTimetableAdapter,
    private val storage: ExcelTimetableStorage,
    private val excelTimetableNormalizerService: ExcelTimetableNormalizerService,
    private val excelTimetableProcessChangesService: ExcelTimetableProcessChangesService,
) {
    fun processFiles(addedOrChanged: List<File>, deleted: List<ExcelFileMetadata>, notChanged: List<File>) {
        val runContext = JobRunContextHolder.get()
            ?: run {
                logger.warn { "No run context found" }
                return
            }

        val runId = runContext.id

//        if (addedOrChanged.isEmpty() && deleted.isEmpty()) {
//            logger.info { "No timetables for update, updating run id for exists timetables" }
//
//            val prevRunId = runContext.prevId
//                ?: run {
//                    logger.warn { "No previous run id found, nothing to process" }
//                    return
//                }
//
//            storage.updateTimetablesRunId(prevRunId, runId)
//            return
//        }

        val ids = (addedOrChanged + notChanged).map {
            excelTimetableAdapter.processAndPersist(it)
        }.flatten()

        logger.info { "Processed ${ids.size} timetables" }

        excelTimetableNormalizerService.normalizeTimetables(ids)

        val prevRunId = runContext.prevId
            ?: run {
                logger.warn { "No previous run id found, nothing to process" }
                storage.showTimetables(ids)
                return
            }

        excelTimetableProcessChangesService.processChangesByRunIds(prevRunId, runId)
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}