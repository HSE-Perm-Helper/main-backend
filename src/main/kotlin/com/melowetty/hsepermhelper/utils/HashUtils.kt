package com.melowetty.hsepermhelper.utils

import java.io.InputStream
import java.security.MessageDigest

class HashUtils {
    companion object {
        fun getHash(inputStream: InputStream): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val hexArray = "0123456789ABCDEF"
            val bytes = digest.digest(inputStream.readAllBytes())
            val hash = StringBuilder(bytes.size * 2)
            bytes.forEach {
                val i = it.toInt()
                hash.append(hexArray[i shr 4 and 0x0f])
                hash.append(hexArray[i and 0x0f])
            }
            return hash.toString()
        }
    }
}