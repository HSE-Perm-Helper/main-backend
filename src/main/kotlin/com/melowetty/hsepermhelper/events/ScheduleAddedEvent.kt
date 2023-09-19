package com.melowetty.hsepermhelper.events

import Schedule
import com.melowetty.hsepermhelper.events.common.PublicEvent
import io.swagger.v3.oas.annotations.media.Schema

class ScheduleAddedEvent(
    @Schema(description = "Расписание, которое было добавлено")
    targetSchedule: Schedule
): PublicEvent()