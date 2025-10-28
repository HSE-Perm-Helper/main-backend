package com.melowetty.hsepermhelper.messaging.event.task

import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo

data class NewTimetableNotifyTask(
    val timetables: List<InternalTimetableInfo>,
) : Task(TaskType.NEW_TIMETABLE_NOTIFY) {
    override fun toString(): String {
        return "NewTimetableNotifyTask(timetables=${timetables.map { it.id }})"
    }
}
