package com.melowetty.hsepermhelper.events

import com.melowetty.hsepermhelper.events.common.PublicEvent
import com.melowetty.hsepermhelper.models.ScheduleInfo
import io.swagger.v3.oas.annotations.media.Schema

class ScheduleChangedForUserEvent(
    @Schema(description = "Список Telegram ID, которым требуется выслать оповещение о изменении в расписании")
    users: List<Long>,
    @Schema(description = "Расписание, в котором произошло измнение")
    targetSchedule: ScheduleInfo,
): PublicEvent()