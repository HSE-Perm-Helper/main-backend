package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelFileMetadataStorage
import com.melowetty.hsepermhelper.timetable.model.ExcelFileMetadata
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class ExcelTimetableFilesProcessService(
    private val storage: ExcelFileMetadataStorage,
    private val excelTimetableProcessService: ExcelTimetableProcessService,
) {
    fun processOrNothing(current: List<File>) {
        val prev = storage.getFilesMetadata()

        val addedOrChanged: MutableList<File> = mutableListOf()
        val deleted: MutableList<ExcelFileMetadata> = mutableListOf()
        val beforeSet = prev.map { it.hash }.toHashSet()
        val afterSet = current.map { it.hashCode }.toHashSet()

        val prevFilesByHash = prev.associateBy { it.hash }
        val currentFilesByHash = current.associateBy { it.hashCode }
        afterSet.forEach { hash ->
            if (!beforeSet.contains(hash)) {
                currentFilesByHash[hash]?.let { addedOrChanged.add(it) }
                    ?: run {
                        logger.warn { "File with hash $hash not found in current files" }
                    }
            }
        }

        beforeSet.forEach { hash: String ->
            if (!afterSet.contains(hash)) {
                prevFilesByHash[hash]?.let { deleted.add(it) }
                    ?: run {
                        logger.warn { "File with hash $hash not found in previous files" }
                    }
            }
        }

        deleted.forEach { storage.deleteById(it.id) }
        storage.save(addedOrChanged)

        if (addedOrChanged.isNotEmpty()) {
            logger.info { "Added or changed ${addedOrChanged.size} files and deleted ${deleted.size} files" }
        } else {
            logger.info { "No files added or changed" }
        }

        val notChanged = prevFilesByHash.mapNotNull { currentFilesByHash[it.key] }

        excelTimetableProcessService.processFiles(addedOrChanged, deleted, notChanged)
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}