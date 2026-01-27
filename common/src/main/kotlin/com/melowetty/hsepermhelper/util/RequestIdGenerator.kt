package com.melowetty.hsepermhelper.util

import java.util.*

object RequestIdGenerator {
    fun generate(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}