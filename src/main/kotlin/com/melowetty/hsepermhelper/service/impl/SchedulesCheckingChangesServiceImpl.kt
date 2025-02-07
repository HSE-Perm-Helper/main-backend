package com.melowetty.hsepermhelper.service.impl

import com.melowetty.hsepermhelper.domain.model.event.ExcelSchedulesChanging
import com.melowetty.hsepermhelper.excel.model.ExcelSchedule
import com.melowetty.hsepermhelper.excel.model.ExcelScheduleDifference
import com.melowetty.hsepermhelper.service.SchedulesCheckingChangesService
import org.springframework.stereotype.Service

@Service
class SchedulesCheckingChangesServiceImpl : SchedulesCheckingChangesService {
    override fun getChanges(before: List<ExcelSchedule>, after: List<ExcelSchedule>): ExcelSchedulesChanging {
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

        val editedSchedules = mutableListOf<ExcelScheduleDifference>()

        for (newSchedule in after) {
            val existsSchedule = before.find {
                checkIsSimilarSchedule(it, newSchedule)
            } ?: continue
            if (existsSchedule != newSchedule) {
                editedSchedules.add(
                    ExcelScheduleDifference(
                        before = existsSchedule,
                        after = newSchedule
                    )
                )
            }
        }
        return ExcelSchedulesChanging(
            added = addedSchedules,
            deleted = deletedSchedules,
            changed = editedSchedules,
        )
    }

    private fun checkIsSimilarSchedule(firstSchedule: ExcelSchedule, secondSchedule: ExcelSchedule): Boolean {
        return firstSchedule.start == secondSchedule.start
                && firstSchedule.end == secondSchedule.end
                && firstSchedule.scheduleType == secondSchedule.scheduleType
    }
}