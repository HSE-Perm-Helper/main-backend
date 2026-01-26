package com.melowetty.hsepermhelper.timetable.compose

import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetable
import com.melowetty.hsepermhelper.domain.model.timetable.TimetableContext

interface EmbeddedTimetable {
    fun embed(user: UserRecord, timetable: InternalTimetable): InternalTimetable
    fun isEmbeddable(user: UserRecord, timetable: InternalTimetable, context: TimetableContext): Boolean
    fun priority(): Int = 0
}