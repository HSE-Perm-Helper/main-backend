package com.melowetty.hsepermhelper.service.impl.timetable

import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import org.springframework.stereotype.Service

@Service
class TimetableChangeDetectionService {
    fun detectAndProcessChanges(
        timetableId: String,
        oldData: List<GroupBasedLesson>,
        newData: List<GroupBasedLesson>
    ) {

    }
}