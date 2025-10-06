package com.melowetty.hsepermhelper.timetable.integration.excel

import com.melowetty.hsepermhelper.domain.model.file.File
import com.melowetty.hsepermhelper.timetable.model.ExcelFileMetadata
import org.apache.commons.lang3.RandomStringUtils

class ExcelFileMetadataStorage {
    private val storage = mutableMapOf<String, ExcelFileMetadata>()

    fun save(file: File) {
        val id = generateId()
        storage[id] = ExcelFileMetadata(id, file.name, file.hashCode)
    }

    fun save(files: List<File>) {
        files.forEach { save(it) }
    }

    fun deleteById(id: String) {
        storage.remove(id)
    }

    fun getFilesMetadata(): List<ExcelFileMetadata> {
        return storage.values.toList()
    }

    private fun generateId(): String {
        return RandomStringUtils.randomAlphanumeric(10)
    }
}