package com.melowetty.hsepermhelper.timetable.compose

import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetable
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableInfo
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableSource
import com.melowetty.hsepermhelper.persistence.projection.UserRecord

interface ParentTimetable {
    fun getTimetables(user: UserRecord): List<InternalTimetableInfo>
    fun get(id: String, user: UserRecord): InternalTimetable
    fun getProcessorType(): InternalTimetableSource
    fun isAvailableForUser(user: UserRecord): Boolean
}