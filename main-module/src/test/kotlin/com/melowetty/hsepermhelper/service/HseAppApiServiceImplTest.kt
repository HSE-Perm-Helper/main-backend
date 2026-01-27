package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.service.impl.HseAppApiServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class HseAppApiServiceImplTest {
    @Test
    fun `test normalize lecturer when he or she have 3 words in full name`() {
        val lecturer = "Иванов Иван Иванович"

        val expected = "Иванов И. И."
        Assertions.assertEquals(expected, HseAppApiServiceImpl.normalizeLecturer(lecturer))
    }

    @Test
    fun `test normalize lecturer when he or she have 1 word in full name`() {
        val lecturer = "Иванов"

        val expected = "Иванов"
        Assertions.assertEquals(expected, HseAppApiServiceImpl.normalizeLecturer(lecturer))
    }

    @Test
    fun `test normalize lecturer when he or she does not have words in full name`() {
        val lecturer = ""

        val expected = ""
        Assertions.assertEquals(expected, HseAppApiServiceImpl.normalizeLecturer(lecturer))
    }
}