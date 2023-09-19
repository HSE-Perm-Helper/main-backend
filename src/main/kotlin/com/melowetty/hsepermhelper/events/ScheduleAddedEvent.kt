package com.melowetty.hsepermhelper.events

import Schedule
import io.swagger.v3.oas.annotations.media.Schema

class ScheduleAddedEvent(
    @Schema(description = "Расписание, которое было добавлено")
    targetSchedule: Schedule
): PublicEvent()