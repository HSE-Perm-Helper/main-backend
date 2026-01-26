package com.melowetty.hsepermhelper.timetable.compose

import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableSource

interface ParentTimetable {
    fun getTimetables(user: UserRecord): List<InternalTimetableInfo>
    fun get(id: String, user: UserRecord): InternalTimetable
    fun getProcessorType(): InternalTimetableSource
    fun isAvailableForUser(user: UserRecord): Boolean
}