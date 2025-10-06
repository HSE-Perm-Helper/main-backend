package com.melowetty.hsepermhelper.timetable

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.domain.model.schedule.Schedule
import com.melowetty.hsepermhelper.domain.model.schedule.ScheduleInfo
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.toSchedule
import com.melowetty.hsepermhelper.timetable.compose.EmbeddedTimetable
import com.melowetty.hsepermhelper.timetable.compose.ParentTimetable
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class TimetableComposer(
    private val timetables: List<ParentTimetable>,
    private val embeddedTimetables: List<EmbeddedTimetable>,
) {
    fun getTimetable(id: String, user: UserDto): Schedule {
        val (originalId, processorType) = TimetableInfoEncoder.decode(id)
        logger.info { "Decoded timetable id $id to original id $originalId and processor type $processorType" }

        val timetableContainer = timetables.firstOrNull { it.getProcessorType() == processorType }
            ?: throw IllegalArgumentException("No such timetable for $processorType")

        val timetable = timetableContainer.get(originalId, user)

        val embedded = embeddedTimetables.filter {
            it.isEmbeddable(user, timetable)
        }

        logger.info { "Found ${embedded.size} embedded timetables" }

        var resultTimetable = timetable
        for (e in embedded) {
            resultTimetable = e.embed(user, resultTimetable)
        }

        return resultTimetable.toSchedule()
    }

    fun getAvailableTimetables(user: UserDto): List<ScheduleInfo> {
        return timetables.mapNotNull {
            if (it.isAvailableForUser(user)) {
                Pair(it.getProcessorType(), it.getTimetables())
            } else null
        }.map {
            throw NotImplementedError("Return schedule info")
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}