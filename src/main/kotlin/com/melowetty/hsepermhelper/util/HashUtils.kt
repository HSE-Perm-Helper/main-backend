package com.melowetty.hsepermhelper.util

import java.math.BigInteger
import java.security.MessageDigest


class HashUtils {
    companion object {
        fun getHash(data: ByteArray): String {
            val hash = MessageDigest.getInstance("MD5").digest(data)
            return BigInteger(1, hash).toString(16)
        }
    }
}