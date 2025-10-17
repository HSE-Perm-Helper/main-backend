package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.event.ExcelSchedulesChanging
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable

interface SchedulesCheckingChangesService {
    fun getChanges(before: List<InternalTimetable>, after: List<InternalTimetable>): ExcelSchedulesChanging
}