package com.melowetty.hsepermhelper.timetable.compose.impl

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.timetable.compose.ParentTimetable
import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableStorage
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableProcessorType
import org.springframework.stereotype.Component

@Component
class BachelorParentTimetable(
    private val storage: ExcelTimetableStorage
) : ParentTimetable {
    override fun getTimetables(): List<InternalTimetableInfo> {
        return storage.getParentTimetables()
    }

    override fun get(id: String, user: UserDto): InternalTimetable {
        return storage.getTimetableFilteredByGroup(id, user.settings.group)
    }

    override fun getProcessorType(): InternalTimetableProcessorType {
        return InternalTimetableProcessorType.BACHELOR_OFFLINE_TIMETABLE
    }

    override fun isAvailableForUser(user: UserDto): Boolean {
        return true
    }
}