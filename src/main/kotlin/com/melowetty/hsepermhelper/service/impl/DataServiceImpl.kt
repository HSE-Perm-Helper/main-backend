package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.entity.DataEntity
import com.melowetty.hsepermhelper.repository.DataRepository
import com.melowetty.hsepermhelper.service.DataService
import com.melowetty.hsepermhelper.util.DateUtils
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class DataServiceImpl(
    private val dataRepository: DataRepository,
) : DataService {
    private val startDate: LocalDateTime = getStartTimeFromRepository()
    private val lastDate: LocalDateTime = getLastTimeFromRepository()
    init {
        saveStartTime()
        saveLastTime()
    }

    private fun saveStartTime() {
        dataRepository.save(
            DataEntity(
                key = SAVED_START_TIME_KEY,
                value = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_PATTERN))
            )
        )
    }

    @Scheduled(fixedRate = 1000 * 60 * 30)
    private fun saveLastTime() {
        dataRepository.save(
            DataEntity(
                key = SAVED_LAST_TIME_KEY,
                value = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_PATTERN))
            )
        )
    }

    override fun getStartTime(): LocalDateTime {
        return startDate
    }

    override fun getLastTime(): LocalDateTime {
        return lastDate
    }

    private fun getStartTimeFromRepository(): LocalDateTime {
        val startTime = dataRepository.findById(SAVED_START_TIME_KEY)
        if(startTime.isEmpty) return LocalDateTime.now()
        else return try {
            LocalDateTime.from(DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_PATTERN).parse(startTime.get().value))
        } catch (e: Exception) {
            e.printStackTrace()
            LocalDateTime.now()
        }
    }

    private fun getLastTimeFromRepository(): LocalDateTime {
        val lastTime = dataRepository.findById(SAVED_LAST_TIME_KEY)
        if(lastTime.isEmpty) return LocalDateTime.now()
        else return try {
            LocalDateTime.from(DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_PATTERN).parse(lastTime.get().value))
        } catch (e: Exception) {
            e.printStackTrace()
            LocalDateTime.now()
        }
    }

    companion object {
        private const val SAVED_START_TIME_KEY = "start_time"
        private const val SAVED_LAST_TIME_KEY = "last_time"
    }
}