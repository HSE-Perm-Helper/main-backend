package com.melowetty.hsepermhelper.persistence.entity

import com.melowetty.hsepermhelper.timetable.model.EducationType
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableSource
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "excel_timetable")
class ExcelTimetableEntity(
    @Id
    val id: String,

    @Column(name = "number")
    val number: Int?,

    @Column(name = "start", nullable = false)
    val start: LocalDate,

    @Column(name = "\"end\"", nullable = false)
    val end: LocalDate,

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: InternalTimetableType,

    @Column(name = "education_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val educationType: EducationType,

    @Column(name = "is_parent", nullable = false)
    val isParent: Boolean,

    @Column(name = "is_visible", nullable = false)
    val isVisible: Boolean,

    @Column(name = "source", nullable = false)
    @Enumerated(EnumType.STRING)
    val source: InternalTimetableSource,

    @Column(name = "lessons_hash", nullable = false)
    val lessonsHash: Int,

    @Column(name = "created", nullable = false)
    val created: LocalDateTime,

    @Column(name = "updated", nullable = false)
    val updated: LocalDateTime,

    runId: String,
) : CreatableFromJob(runId) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is ExcelTimetableEntity && other.id == id
    }

    override fun toString(): String {
        return "ExcelTimetableEntity(id='$id', number=$number, start=$start, end=$end, type=$type)"
    }
}