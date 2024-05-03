package com.melowetty.hsepermhelper.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class HashUtilsTest {
    @Test
    fun `test hashes when same files but open in different input stream`() {
        val firstInputStream = TestUtils.readFileAsInputStream("/utils/file_1.xls")
        val secondInputStream = TestUtils.readFileAsInputStream("/utils/file_1.xls")
        val firstHash = HashUtils.getHash(firstInputStream)
        val secondHash = HashUtils.getHash(secondInputStream)
        assertEquals(firstHash, secondHash)
    }

    @Test
    fun `test hashes when same files but open in one input stream`() {
        val inputStream = TestUtils.readFileAsInputStream("/utils/file_1.xls")
        val firstHash = HashUtils.getHash(inputStream)
        val secondHash = HashUtils.getHash(inputStream)
        assertEquals(firstHash, secondHash)
    }

    @Test
    fun `test hashes when files is different`() {
        val firstInputStream = TestUtils.readFileAsInputStream("/utils/file_1.xls")
        val secondInputStream = TestUtils.readFileAsInputStream("/utils/file_2.xls")
        val firstHash = HashUtils.getHash(firstInputStream)
        val secondHash = HashUtils.getHash(secondInputStream)
        assertNotEquals(firstHash, secondHash)
    }
}