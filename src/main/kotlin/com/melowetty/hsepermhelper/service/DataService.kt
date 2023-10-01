package com.melowetty.hsepermhelper.service

interface DataService {
    /**
     * Get last schedules hashcode, which were fetched
     *
     * @return list of saved schedules hashcode
     */
    fun getSavedSchedulesHashcode(): List<String>

    /**
     * Save schedules hash code
     *
     * @param hashcode schedules hashcode
     */
    fun saveSchedulesHashcode(hashcode: List<String>)
}