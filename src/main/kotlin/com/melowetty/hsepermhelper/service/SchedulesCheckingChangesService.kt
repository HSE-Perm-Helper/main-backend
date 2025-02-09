package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.event.ExcelSchedulesChanging
import com.melowetty.hsepermhelper.excel.model.ExcelSchedule

interface SchedulesCheckingChangesService {
    fun getChanges(before: List<ExcelSchedule>, after: List<ExcelSchedule>): ExcelSchedulesChanging
}