package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.ScheduleDifference
import com.melowetty.hsepermhelper.model.SchedulesChanging
import com.melowetty.hsepermhelper.service.SchedulesCheckingChangesService
import org.springframework.stereotype.Service

@Service
class SchedulesCheckingChangesServiceImpl: SchedulesCheckingChangesService {
    override fun getChanges(before: List<Schedule>, after: List<Schedule>): SchedulesChanging {
        val deletedSchedules = before.filter { after.contains(it).not() }
        val addedSchedules = after.filter { before.contains(it) }
        val editedSchedules = mutableListOf<ScheduleDifference>()

        for (newSchedule in after) {
            val existsSchedule = before.find {
                it.start == newSchedule.start  && it.end == newSchedule.end && it.scheduleType == newSchedule.scheduleType
            } ?: continue
            if(existsSchedule != newSchedule) {
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

        )
    }
}