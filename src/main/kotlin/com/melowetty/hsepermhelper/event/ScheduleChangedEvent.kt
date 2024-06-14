package com.melowetty.hsepermhelper.event

import com.melowetty.hsepermhelper.model.ChangedSchedule

data class ScheduleChangedEvent(
    val changes: Map<EventType, List<ChangedSchedule>>,
)