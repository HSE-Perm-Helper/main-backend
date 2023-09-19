package com.melowetty.hsepermhelper.events.internal

import com.melowetty.hsepermhelper.events.common.EventType
import com.melowetty.hsepermhelper.models.ChangedSchedule

data class ScheduleChangedEvent(
    val changes: Map<EventType, List<ChangedSchedule>>,
)