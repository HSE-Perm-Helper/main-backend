package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.Schedule
import com.melowetty.hsepermhelper.model.SchedulesChanging

interface SchedulesCheckingChangesService {
    fun getChanges(before: List<Schedule>, after: List<Schedule>): SchedulesChanging
}