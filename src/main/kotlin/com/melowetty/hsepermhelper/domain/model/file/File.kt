package com.melowetty.hsepermhelper.domain.model.file

import com.melowetty.hsepermhelper.util.HashUtils
import java.io.ByteArrayInputStream
import java.io.InputStream

data class File(
    val data: ByteArray,
    val name: String,
    val hashCode: String = HashUtils.getHash(data),
) {
    override fun hashCode(): Int {
        return hashCode.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        return hashCode == other.hashCode
    }

    fun toInputStream(): InputStream {
        return ByteArrayInputStream(data)
    }

    override fun toString(): String {
        return "File(hashCode='$hashCode')"
    }
}