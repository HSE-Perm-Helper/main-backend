package com.melowetty.hsepermhelper.events

import com.melowetty.hsepermhelper.events.common.PublicEvent
import com.melowetty.hsepermhelper.models.ScheduleInfo
import io.swagger.v3.oas.annotations.media.Schema

data class ScheduleChangedForUserEvent(
    @Schema(description = "Информация о расписании, в котором произошло изменение")
    val targetSchedule: ScheduleInfo,
    @Schema(description = "Список Telegram ID, которым требуется выслать оповещение о изменении в расписании")
    val users: List<Long>,
): PublicEvent()