package com.melowetty.hsepermhelper.events

import Schedule
import io.swagger.v3.oas.annotations.media.Schema

class ScheduleChangedForUserEvent(
    id: Long,
    @Schema(description = "Список Telegram ID, которым требуется выслать оповещение о изменении в расписании")
    users: List<Long>,
    @Schema(description = "Расписание, в котором произошло измнение")
    targetSchedule: Schedule,
): PublicEvent(id)