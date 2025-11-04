package com.melowetty.hsepermhelper.timetable.compose.impl

import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.timetable.compose.ParentTimetable
import com.melowetty.hsepermhelper.timetable.integration.excel.ExcelTimetableStorage
import com.melowetty.hsepermhelper.timetable.model.InternalTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableSource
import org.springframework.stereotype.Component

@Component
class ExcelParentTimetable(
    private val storage: ExcelTimetableStorage
) : ParentTimetable {
    // TODO: добавить разделение по уровню образования

    override fun getTimetables(): List<InternalTimetableInfo> {
        return storage.getParentTimetables()
    }

    override fun get(id: String, user: UserRecord): InternalTimetable {
        return storage.getTimetableFilteredByGroup(id, user.educationGroup.group)
    }

    override fun getProcessorType(): InternalTimetableSource {
        return InternalTimetableSource.EXCEL
    }

    override fun isAvailableForUser(user: UserRecord): Boolean {
        return true
    }
}