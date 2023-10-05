package com.melowetty.hsepermhelper.service.impl

import Schedule
import com.melowetty.hsepermhelper.entity.DataEntity
import com.melowetty.hsepermhelper.models.ScheduleInfo
import com.melowetty.hsepermhelper.models.ScheduleType
import com.melowetty.hsepermhelper.repository.DataRepository
import com.melowetty.hsepermhelper.service.DataService
import com.melowetty.hsepermhelper.utils.DateUtils
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class DataServiceImpl(
    private val dataRepository: DataRepository,
) : DataService {
    val startDate: LocalDateTime = getStartTimeFromRepository()
    init {
        saveStartTime()
    }
    override fun getSavedSchedules(): List<ScheduleInfo> {
        val savedSchedulesKey = dataRepository.findById(SAVED_SCHEDULES_KEY)
        if(savedSchedulesKey.isEmpty.not()) {
            return savedSchedulesKey.get().value.split(SAVED_SCHEDULES_DELIMITER)
                .map {
                    val (weekNumber, scheduleType, weekStart, weekEnd, hashcode) = it.split("|")
                    ScheduleInfo(
                        weekNumber = weekNumber.toIntOrNull(),
                        scheduleType = ScheduleType.valueOf(scheduleType),
                        weekStart = LocalDate.parse(weekStart),
                        weekEnd = LocalDate.parse(weekEnd),
                    )
                }
        }
        return listOf()
    }

    private fun saveSchedulesHashcode(hashcode: List<String>) {
        dataRepository.save(dataRepository.findById(SAVED_SCHEDULES_KEY).get()
            .copy(value = hashcode.joinToString(separator = SAVED_SCHEDULES_DELIMITER))
        )
    }

    override fun saveSchedules(schedules: List<Schedule>) {
        saveSchedulesHashcode(
            schedules.map { "${it.weekNumber ?: ""}|${it.scheduleType.name}|${it.weekStart}|${it.weekEnd}|${it.hashCode()}" }
        )
    }

    private fun saveStartTime() {
        dataRepository.save(
            DataEntity(
                key = SAVED_START_TIME_KEY,
                value = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_PATTERN))
            )
        )
    }

    override fun getStartTime(): LocalDateTime {
        return startDate
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

    companion object {
        private const val SAVED_SCHEDULES_KEY = "saved_schedules"
        private const val SAVED_SCHEDULES_DELIMITER = ","
        private const val SAVED_START_TIME_KEY = "start_time"
    }
}