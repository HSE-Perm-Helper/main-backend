package com.melowetty.hsepermhelper.utils

import com.melowetty.hsepermhelper.models.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class TestUtils {
    companion object {
        fun readFile(file: String): File {
            return File(data = readFileAsInputStream(file).readAllBytes())
        }

        fun readFileAsInputStream(file: String): InputStream {
            return Files.newInputStream(Path.of("src/test/resources/$file"))
        }
    }
}