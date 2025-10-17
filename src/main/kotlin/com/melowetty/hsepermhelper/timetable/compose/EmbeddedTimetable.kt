package com.melowetty.hsepermhelper.timetable.compose

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.TimetableContext

interface EmbeddedTimetable {
    fun embed(user: UserDto, timetable: InternalTimetable): InternalTimetable
    fun isEmbeddable(user: UserDto, timetable: InternalTimetable, context: TimetableContext): Boolean
    fun priority(): Int = 0
}