package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.repository.DataRepository
import com.melowetty.hsepermhelper.service.DataService
import org.springframework.stereotype.Service

@Service
class DataServiceImpl(
    private val dataRepository: DataRepository,
) : DataService {
    override fun getSavedSchedulesHashcode(): List<String> {
        val savedSchedulesKey = dataRepository.findById(SAVED_SCHEDULES_KEY)
        if(savedSchedulesKey.isEmpty.not()) {
            return savedSchedulesKey.get().value.split(SAVED_SCHEDULES_DELIMITER)
        }
        return listOf()
    }

    override fun saveSchedulesHashcode(hashcode: List<String>) {
        dataRepository.save(dataRepository.findById(SAVED_SCHEDULES_KEY).get()
            .copy(value = hashcode.joinToString(separator = SAVED_SCHEDULES_DELIMITER))
        )
    }
    companion object {
        private const val SAVED_SCHEDULES_KEY = "saved_schedules"
        private const val SAVED_SCHEDULES_DELIMITER = ","
    }
}