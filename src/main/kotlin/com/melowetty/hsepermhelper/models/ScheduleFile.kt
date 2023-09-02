package com.melowetty.hsepermhelper.models

import com.melowetty.hsepermhelper.utils.HashUtils
import java.io.InputStream

data class ScheduleFile(
    val file: InputStream,
    val hashCode: String = HashUtils.getHash(file),
)