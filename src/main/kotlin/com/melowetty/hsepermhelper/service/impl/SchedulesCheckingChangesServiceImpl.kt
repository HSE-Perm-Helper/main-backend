package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.schedule.ScheduleDifference
import com.melowetty.hsepermhelper.model.event.SchedulesChanging
import com.melowetty.hsepermhelper.service.SchedulesCheckingChangesService
import org.springframework.stereotype.Service

@Service
class SchedulesCheckingChangesServiceImpl : SchedulesCheckingChangesService {
    override fun getChanges(before: List<Schedule>, after: List<Schedule>): SchedulesChanging {
        val deletedSchedules = before.filter { schedule ->
            after.find {
                checkIsSimilarSchedule(it, schedule)
            } == null
        }

        val addedSchedules = after.filter { schedule ->
            before.find {
                checkIsSimilarSchedule(it, schedule)
            } == null
        }

        val editedSchedules = mutableListOf<ScheduleDifference>()

        for (newSchedule in after) {
            val existsSchedule = before.find {
                checkIsSimilarSchedule(it, newSchedule)
            } ?: continue
            if (existsSchedule != newSchedule) {
                editedSchedules.add(
                    ScheduleDifference(
                        before = existsSchedule,
                        after = newSchedule
                    )
                )
            }
        }
        return SchedulesChanging(
            added = addedSchedules,
            deleted = deletedSchedules,
            changed = editedSchedules,
        )
    }

    private fun checkIsSimilarSchedule(firstSchedule: Schedule, secondSchedule: Schedule): Boolean {
        return firstSchedule.start == secondSchedule.start
                && firstSchedule.end == secondSchedule.end
                && firstSchedule.scheduleType == secondSchedule.scheduleType
    }
}