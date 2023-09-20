package com.melowetty.hsepermhelper.events

import com.melowetty.hsepermhelper.events.common.PublicEvent
import com.melowetty.hsepermhelper.models.ScheduleInfo
import io.swagger.v3.oas.annotations.media.Schema

data class ScheduleAddedEvent(
    @Schema(description = "Расписание, которое было добавлено")
    val targetSchedule: ScheduleInfo
): PublicEvent()