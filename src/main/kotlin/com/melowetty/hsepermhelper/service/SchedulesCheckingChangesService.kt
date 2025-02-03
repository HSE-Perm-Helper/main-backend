package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.event.ExcelSchedulesChanging
import com.melowetty.hsepermhelper.model.schedule.Schedule

interface SchedulesCheckingChangesService {
    fun getChanges(before: List<Schedule>, after: List<Schedule>): ExcelSchedulesChanging
}