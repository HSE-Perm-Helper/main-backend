package com.melowetty.hsepermhelper.persistence.projection

import com.melowetty.hsepermhelper.persistence.entity.EducationGroupEntity
import com.melowetty.hsepermhelper.domain.model.timetable.EducationType

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
