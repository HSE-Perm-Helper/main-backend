package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.event.ExcelSchedulesChanging
import com.melowetty.hsepermhelper.model.excel.ExcelSchedule

interface SchedulesCheckingChangesService {
    fun getChanges(before: List<ExcelSchedule>, after: List<ExcelSchedule>): ExcelSchedulesChanging
}