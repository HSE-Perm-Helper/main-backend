package com.melowetty.hsepermhelper.persistence.entity

import com.melowetty.hsepermhelper.domain.model.timetable.impl.GroupBasedLesson
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "group_lessons")
class GroupLessonsEntity(
    @EmbeddedId
    val id: GroupLessonsEntityId,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lessons", nullable = false)
    val lessons: List<GroupBasedLesson>
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is GroupLessonsEntity && other.id == id
    }

    override fun toString(): String = "GroupLessonsEntity(id=$id)"
}

@Embeddable
data class GroupLessonsEntityId(
    @Column(name = "timetable_id", nullable = false)
    val timetableId: String,

    @Column(name = "\"group\"", nullable = false)
    val group: String
)