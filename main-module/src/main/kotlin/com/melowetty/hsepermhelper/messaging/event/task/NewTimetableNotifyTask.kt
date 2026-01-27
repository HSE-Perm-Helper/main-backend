package com.melowetty.hsepermhelper.messaging.event.task

import com.fasterxml.jackson.annotation.JsonTypeName
import com.melowetty.hsepermhelper.domain.model.timetable.InternalTimetableInfo

@JsonTypeName("NEW_TIMETABLE_NOTIFY")
data class NewTimetableNotifyTask(
    val timetables: List<InternalTimetableInfo>,
) : Task(TaskType.NEW_TIMETABLE_NOTIFY) {
    override fun toString(): String {
        return "NewTimetableNotifyTask(timetables=${timetables.map { it.id }})"
    }
}
