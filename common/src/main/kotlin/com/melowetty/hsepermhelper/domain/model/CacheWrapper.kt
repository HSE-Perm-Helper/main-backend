package com.melowetty.hsepermhelper.domain.model

class CacheWrapper<T>(
    private val cacheEviction: Long = 3600,
    private val cacheFunction: () -> T
) {
    private var data: T? = null

    private var lastUpdate: Long? = null

    fun get(): T {
        if (lastUpdate == null || lastUpdate!! + cacheEviction * 1000 < System.currentTimeMillis()) {
            data = cacheFunction()
            lastUpdate = System.currentTimeMillis()
        }

        return data ?: throw IllegalStateException("Incorrect cache state")
    }
}