package com.melowetty.hsepermhelper.persistence.storage

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.persistence.entity.ExcelFileMetadataEntity
import com.melowetty.hsepermhelper.persistence.repository.ExcelFileMetadataRepository
import com.melowetty.hsepermhelper.timetable.model.ExcelFileMetadata
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component

@Component
class ExcelFileMetadataStorage(
    private val excelFileMetadataRepository: ExcelFileMetadataRepository,
) {
    fun save(files: List<File>) {
        val entities = files.map {
            val id = generateId()
            val entity = ExcelFileMetadataEntity(
                id = id,
                name = it.name,
                hash = it.hashCode,
            )
            entity
        }

        excelFileMetadataRepository.saveAll(entities)
    }

    fun deleteById(id: String) {
        excelFileMetadataRepository.deleteById(id)
    }

    fun getFilesMetadata(): List<ExcelFileMetadata> {
        return excelFileMetadataRepository.findAll().map {
            ExcelFileMetadata(
                id = it.id,
                name = it.name,
                hash = it.hash,
            )
        }
    }

    private fun generateId(): String {
        return RandomStringUtils.randomAlphanumeric(6).lowercase()
    }
}