package com.melowetty.hsepermhelper.models

import com.melowetty.hsepermhelper.utils.HashUtils
import java.io.InputStream

data class File(
    val inputStream: InputStream,
    val hashCode: String = HashUtils.getHash(inputStream),
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
}