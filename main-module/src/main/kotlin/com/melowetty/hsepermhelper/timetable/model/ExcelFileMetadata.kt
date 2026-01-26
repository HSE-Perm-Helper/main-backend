package com.melowetty.hsepermhelper.timetable.model

data class ExcelFileMetadata(
    val id: String,
    val name: String,
    val hash: String,
) {
    override fun hashCode(): Int {
        return hash.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return hash == (other as? ExcelFileMetadata)?.hash
    }
}
