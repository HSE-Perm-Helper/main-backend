package com.melowetty.hsepermhelper.messaging.event.task

import com.fasterxml.jackson.annotation.JsonTypeName
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson

@JsonTypeName("CHANGE_DETECTION")
data class ChangeDetectionTask(
    val timetableId: String,
    val oldData: List<GroupBasedLesson>,
    val newData: List<GroupBasedLesson>
) : Task(TaskType.CHANGE_DETECTION) {
    override fun toString(): String {
        return "ChangeDetectionTask(timetableId=$timetableId)"
    }
}
