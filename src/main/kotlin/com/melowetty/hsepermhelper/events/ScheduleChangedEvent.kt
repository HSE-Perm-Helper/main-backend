package com.melowetty.hsepermhelper.events

import Schedule

data class ScheduleChangedEvent(
    val changes: Map<EventType, List<Schedule>>,
)