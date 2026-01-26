package com.melowetty.hsepermhelper.persistence.projection

import com.melowetty.hsepermhelper.domain.model.user.EducationGroupEntity
import com.melowetty.hsepermhelper.timetable.model.EducationType

data class EducationGroupRecord(
    val group: String,
    val educationType: EducationType,
) {
    companion object {
        fun from(entity: EducationGroupEntity): EducationGroupRecord {
            return EducationGroupRecord(entity.group, entity.educationType)
        }
    }
}
