package com.melowetty.hsepermhelper.timetable.compose

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableProcessorType

interface ParentTimetable {
    fun getTimetables(): List<InternalTimetableInfo>
    fun get(id: String, user: UserDto): InternalTimetable
    fun getProcessorType(): InternalTimetableProcessorType
    fun isAvailableForUser(user: UserDto): Boolean
}