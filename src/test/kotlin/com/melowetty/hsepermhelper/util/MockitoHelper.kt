package com.melowetty.hsepermhelper.util

import org.mockito.Mockito

class MockitoHelper {
    companion object MockitoHelper {
        fun <T> anyObject(): T {
            Mockito.any<T>()
            return uninitialized()
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> uninitialized(): T = null as T
    }
}