package com.melowetty.hsepermhelper.timetable.compose

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable

interface EmbeddedTimetable {
    fun embed(user: UserDto, timetable: InternalTimetable): InternalTimetable
    fun isEmbeddable(user: UserDto, timetable: InternalTimetable): Boolean
}