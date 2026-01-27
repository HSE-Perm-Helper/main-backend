package com.melowetty.hsepermhelper.util

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class TestUtils {
    companion object {
        fun readFileAsInputStream(file: String): InputStream {
            return Files.newInputStream(Path.of("src/test/resources/$file"))
        }
    }
}