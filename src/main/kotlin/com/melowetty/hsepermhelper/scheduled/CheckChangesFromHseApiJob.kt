package com.melowetty.hsepermhelper.scheduled

import com.melowetty.hsepermhelper.domain.entity.UserEntity
import com.melowetty.hsepermhelper.domain.model.hseapp.HseAppLesson
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleType
import com.melowetty.hsepermhelper.notification.ScheduleChangedForUserNotification
import com.melowetty.hsepermhelper.repository.UserRepository
import com.melowetty.hsepermhelper.service.ExcelScheduleService
import com.melowetty.hsepermhelper.service.HseAppApiService
import com.melowetty.hsepermhelper.service.NotificationService
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CheckChangesFromHseApiJob(
    private val hseAppApiService: HseAppApiService,
    private val scheduleService: ExcelScheduleService,
    private val userRepository: UserRepository,
    @Qualifier("check-changes-from-hse-api-executor-service")
    private val executorService: ExecutorService,
    private val notificationService: NotificationService
) {
    private val prevLessonsHash: MutableMap<UUID, Int> = mutableMapOf()
    private val prevMinorHash: MutableMap<UUID, Map<ScheduleInfo, Int>> = mutableMapOf()

    fun getScheduleRange(schedules: List<ScheduleInfo>): Pair<LocalDate, LocalDate>? {
        val weekSchedules = schedules.filterNot { it.scheduleType == ScheduleType.QUARTER_SCHEDULE }

        val start = weekSchedules.minOfOrNull { it.start } ?: return null
        val end = weekSchedules.maxOfOrNull { it.end } ?: return null

        return Pair(start, end)
    }

    @Scheduled(fixedRate = 1000 * 60 * 60, initialDelay = 1000 * 60 * 10)
    fun checkChangesFromHseApi() {
        val startPage = 1
        val pageSize = 50

        val schedules = scheduleService.getAvailableSchedules()
            .filterNot { it.scheduleType == ScheduleType.QUARTER_SCHEDULE }

        val (start, end) = getScheduleRange(schedules) ?: return

        val pageable = Pageable.ofSize(pageSize)

        val users = userRepository.findByEmailNotNull(pageable)

        val tasks = users.map {
            Callable {
                processUser(it, schedules, start, end)
            }
        }.toList()

        executorService.invokeAll(tasks)

        for(page in startPage + 1 .. users.totalPages) {
            val pagedTasks = userRepository.findByEmailNotNull(
                Pageable.ofSize(pageSize).withPage(page)
            ).map {
                Callable {
                    processUser(it, schedules, start, end)
                }
            }.toList()

            executorService.invokeAll(pagedTasks)
        }
    }

    private fun processUser(user: UserEntity, schedules: List<ScheduleInfo>, start: LocalDate, end: LocalDate) {
        try {
            val lessons = hseAppApiService.directGetLessons(user.email!!, start, end)

            val hash = lessons.hashCode()
            val prevHash = prevLessonsHash.getOrDefault(user.id, hash)

            prevLessonsHash[user.id] = hash

            if (prevMinorHash.containsKey(user.id).not()) {
                val lessons = getMinorLessons(lessons)
                prevMinorHash[user.id] = getMinorLessonsMap(schedules, lessons)
            }

            if (hash != prevHash) {
                checkChanges(user, schedules, lessons)
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
            return
        }
    }

    private fun getMinorLessonsMap(schedules: List<ScheduleInfo>, lessons: List<HseAppLesson>): Map<ScheduleInfo, Int> {
        val result = mutableMapOf<ScheduleInfo, Int>()

        schedules.forEach {
            val range = it.start.rangeTo(it.end)

            val currentLessons = lessons.filter { range.contains(it.dateStart.toLocalDate()) }

            result[it] = currentLessons.hashCode()
        }

        return result
    }

    private fun getMinorLessons(lessons: List<HseAppLesson>): List<HseAppLesson> {
        return lessons.filter { it.isMinor }
    }

    private fun checkChanges(
        user: UserEntity,
        schedules: List<ScheduleInfo>,
        lessons: List<HseAppLesson>
    ) {
        val minorLessons = getMinorLessons(lessons)

        val dayOfWeek = minorLessons.firstOrNull()?.dateStart?.dayOfWeek ?: return

        val hashes = getMinorLessonsMap(schedules, minorLessons)

        val prevHashes = prevMinorHash[user.id] ?: return

        hashes
            .filter { prevHashes.containsKey(it.key) }
            .filter { prevHashes[it.key] != it.value }
            .map { it.key }
            .forEach {
                val scheduleChangedEvent = ScheduleChangedForUserNotification(
                    targetSchedule = it,
                    users = listOf(user.telegramId),
                    differentDays = listOf(dayOfWeek)
                )
                notificationService.sendNotification(scheduleChangedEvent)
            }

        prevMinorHash[user.id] = hashes
    }
}