package com.melowetty.hsepermhelper.timetable

import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toSchedule
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toScheduleType
import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.timetable.compose.EmbeddedTimetable
import com.melowetty.hsepermhelper.timetable.compose.ParentTimetable
import com.melowetty.hsepermhelper.timetable.model.TimetableContext
import com.melowetty.hsepermhelper.timetable.model.TimetablePurpose
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class TimetableComposer(
    private val timetables: List<ParentTimetable>,
    private val embeddedTimetables: List<EmbeddedTimetable>,
) {
    fun getTimetable(id: String, user: UserRecord): Schedule {
        return internalGetTimetable(id, user, TimetableContext(TimetablePurpose.DISPLAY))
    }

    fun getAllLessons(user: UserRecord): List<Lesson> {
        val context = TimetableContext(TimetablePurpose.SETTINGS)

        val timetables = timetables.mapNotNull { timetable ->
            if (timetable.isAvailableForUser(user)) {
                timetable.getTimetables(user).map {
                    it.copy(id = TimetableInfoEncoder.encode(it.id, timetable.getProcessorType()))
                }
            } else null
        }.flatten().sortedBy { it.start }

        val timetablesLimitCountByType = timetables.map { it.type }.associateWith { it.limitForSettings }
        val currentTimetableCountByType = timetables.map { it.type }.associateWith { 0 }.toMutableMap()

        val filteredTimetables = timetables.filter {
            val currentCount = currentTimetableCountByType[it.type]!!
            val limitCount = timetablesLimitCountByType[it.type]

            if (limitCount != null && currentCount > limitCount) {
                false
            } else {
                currentTimetableCountByType[it.type] = currentCount + 1
                true
            }
        }

        return filteredTimetables.asSequence().map {
            internalGetTimetable(it.id, user, context)
        }.map { it.lessons }.flatten().distinct().sortedBy { it.time }.toList()
    }

    private fun internalGetTimetable(id: String, user: UserRecord, context: TimetableContext): Schedule {
        val (originalId, processorType) = TimetableInfoEncoder.decode(id)
        logger.info { "Decoded timetable id $id to original id $originalId and processor type $processorType" }

        val timetableContainer = timetables.firstOrNull { it.getProcessorType() == processorType }
            ?: throw IllegalArgumentException("No such timetable for $processorType")

        val timetable = timetableContainer.get(originalId, user)

        val embedded = embeddedTimetables.filter {
            it.isEmbeddable(user, timetable, context)
        }.sortedByDescending { it.priority() }

        logger.info { "Found ${embedded.size} embedded timetables" }

        var resultTimetable = timetable
        for (e in embedded) {
            resultTimetable = e.embed(user, resultTimetable)
        }

        resultTimetable.id = id

        return resultTimetable.toSchedule()
    }

    fun getAvailableTimetables(user: UserRecord): List<ScheduleInfo> {
        return timetables.mapNotNull { timetable ->
            if (timetable.isAvailableForUser(user)) {
                timetable.getTimetables(user).map {
                    ScheduleInfo(
                        id = TimetableInfoEncoder.encode(it.id, timetable.getProcessorType()),
                        number = it.number,
                        start = it.start,
                        end = it.end,
                        scheduleType = it.type.toScheduleType(),
                    )
                }
            } else null
        }.flatten().sortedWith(compareBy({ it.start }, { it.end }))
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}