package com.melowetty.hsepermhelper.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class HashUtilsTest {
    @Test
    fun `test hashes when same files but open in different input stream`() {
        val firstArray = TestUtils.readFileAsInputStream("/utils/file_1.xls").readAllBytes()
        val secondArray = TestUtils.readFileAsInputStream("/utils/file_1.xls").readAllBytes()
        val firstHash = HashUtils.getHash(firstArray)
        val secondHash = HashUtils.getHash(secondArray)
        assertEquals(firstHash, secondHash)
    }

    @Test
    fun `test hashes when same files but open in one input stream`() {
        val file = TestUtils.readFileAsInputStream("/utils/file_1.xls").readAllBytes()
        val firstHash = HashUtils.getHash(file)
        val secondHash = HashUtils.getHash(file)
        assertEquals(firstHash, secondHash)
    }

    @Test
    fun `test hashes when files is different`() {
        val firstArray = TestUtils.readFileAsInputStream("/utils/file_1.xls").readAllBytes()
        val secondArray = TestUtils.readFileAsInputStream("/utils/file_2.xls").readAllBytes()
        val firstHash = HashUtils.getHash(firstArray)
        val secondHash = HashUtils.getHash(secondArray)
        assertNotEquals(firstHash, secondHash)
    }
}