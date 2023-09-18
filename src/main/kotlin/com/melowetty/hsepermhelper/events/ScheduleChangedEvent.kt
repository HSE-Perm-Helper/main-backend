package com.melowetty.hsepermhelper.events

import Schedule
import com.melowetty.hsepermhelper.models.ChangedSchedule

data class ScheduleChangedEvent(
    val changes: Map<EventType, List<ChangedSchedule>>,
)