package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.schedule.Schedule
import com.melowetty.hsepermhelper.model.event.SchedulesChanging

interface SchedulesCheckingChangesService {
    fun getChanges(before: List<Schedule>, after: List<Schedule>): SchedulesChanging
}