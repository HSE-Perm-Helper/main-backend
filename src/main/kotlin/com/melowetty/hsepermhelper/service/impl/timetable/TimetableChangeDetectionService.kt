package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.computeHash
import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import com.melowetty.hsepermhelper.timetable.compose.impl.HiddenLessonsEmbeddedTimetable
import com.melowetty.hsepermhelper.persistence.storage.ExcelTimetableStorage
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import com.melowetty.hsepermhelper.util.Paginator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime

@Service
class TimetableChangeDetectionService(
    private val timetableStorage: ExcelTimetableStorage,
    private val userStorage: UserStorage,
    private val hiddenLessonsEmbeddedTimetable: HiddenLessonsEmbeddedTimetable,
    private val timetableNotificationService: TimetableNotificationService,
) {
    fun detectAndProcessChanges(
        timetableId: String,
        oldData: List<GroupBasedLesson>,
        newData: List<GroupBasedLesson>
    ) {
        val timetableInfo = timetableStorage.getTimetableInfo(timetableId)

        val groupedOldData = oldData.groupBy { it.group }
        val groupedNewData = newData.groupBy { it.group }

        val groups = groupedOldData.keys + groupedNewData.keys

        for (group in groups) {
            processGroupChanges(
                timetableInfo,
                group,
                groupedOldData[group] ?: emptyList(),
                groupedNewData[group] ?: emptyList()
            )
        }
    }

    fun processGroupChanges(
        timetableInfo: InternalTimetableInfo,
        group: String,
        oldLessons: List<GroupBasedLesson>,
        newLessons: List<GroupBasedLesson>,
    ) {
        if (oldLessons.computeHash() == newLessons.computeHash()) {
            logger.trace { "No changes detected for group $group" }
            return
        }

        Paginator.fetchPageable(
            limit = 1,
            fetchFunction = { limit, token ->
                userStorage.findUsersAfterId(
                    token,
                    limit,
                    educationGroup = group,
                    educationType = timetableInfo.educationType,
                    isEnabledChangedSchedule = true,
                    options = UserStorage.Options(
                        withHiddenLessons = true
                    )
                )
            }
        ) { users ->
            val groupedUsers = groupByHiddenLessons(users)

            groupedUsers.forEach { users ->
                val user = users.firstOrNull()
                    ?: return@forEach

                val oldTimetable = makeTimetable(timetableInfo, oldLessons)
                val newTimetable = makeTimetable(timetableInfo, newLessons)

                val filteredOldTimetable = hiddenLessonsEmbeddedTimetable.embed(user, oldTimetable)
                val filteredNewTimetable = hiddenLessonsEmbeddedTimetable.embed(user, newTimetable)

                val changedDays = getChangesByDays(filteredOldTimetable, filteredNewTimetable)

                timetableNotificationService.notifyAboutChangedTimetable(
                    timetableInfo, changedDays, users.map { it.id }
                )
            }
        }
    }

    private fun getChangesByDays(oldTimetable: InternalTimetable, newTimetable: InternalTimetable): List<DayOfWeek> {
        val oldLessonsGroupedByDay = oldTimetable.lessons.groupBy { it.time.dayOfWeek }
        val newLessonsGroupedByDay = newTimetable.lessons.groupBy { it.time.dayOfWeek }

        val days = oldLessonsGroupedByDay.keys + newLessonsGroupedByDay.keys

        val changedDays = mutableListOf<DayOfWeek>()

        for (day in days) {
            val oldHash = oldLessonsGroupedByDay[day]?.computeHash() ?: 0
            val newHash = newLessonsGroupedByDay[day]?.computeHash() ?: 0

            if (oldHash != newHash) {
                changedDays.add(day)
            }
        }

        return changedDays.sortedBy { it.value }
    }

    private fun makeTimetable(info: InternalTimetableInfo, lessons: List<GroupBasedLesson>): ExcelTimetable {
        return ExcelTimetable(
            id = info.id,
            number = info.number,
            lessons = lessons,
            start = info.start,
            end = info.end,
            type = info.type,
            educationType = info.educationType,
            isParent = info.isParent,
            source = info.source,
            created = LocalDateTime.now(),
            updated = LocalDateTime.now(),
        )
    }

    private fun groupByHiddenLessons(users: List<UserRecord>): List<List<UserRecord>> {
        return users.groupBy { it.hiddenLessons.sumOf { it.hashCode() } }.map { it.value }
    }

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}