package com.melowetty.hsepermhelper.events

import com.melowetty.hsepermhelper.events.common.PublicEvent
import io.swagger.v3.oas.annotations.media.Schema

data class ScheduleChangedForUserEvent(
    @Schema(description = "Список Telegram ID, которым требуется выслать оповещение о изменении в расписании")
    val users: List<Long>,
): PublicEvent()