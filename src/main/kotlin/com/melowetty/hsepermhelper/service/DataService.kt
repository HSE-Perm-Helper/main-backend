package com.melowetty.hsepermhelper.service

import java.time.LocalDateTime

interface DataService {
    /**
     * Get last start time of server
     *
     * @return start time of server
     */
    fun getStartTime(): LocalDateTime

    /**
     * Get last time of server work
     *
     * @return last time of server
     */
    fun getLastTime(): LocalDateTime
}