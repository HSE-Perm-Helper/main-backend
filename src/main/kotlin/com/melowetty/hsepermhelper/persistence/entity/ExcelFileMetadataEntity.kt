package com.melowetty.hsepermhelper.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "excel_file_metadata")
class ExcelFileMetadataEntity(
    @Id
    val id: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "hash", nullable = false, length = 32)
    val hash: String,
) {
    override fun toString(): String = "ExcelFileMetadataEntity(id=$id, name='$name', hash='$hash')"

    override fun equals(other: Any?): Boolean {
        return other is ExcelFileMetadataEntity && other.id == id
    }

    override fun hashCode(): Int = id.hashCode()
}