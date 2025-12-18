package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.persistence.entity.GroupLessonsEntity
import com.melowetty.hsepermhelper.persistence.entity.GroupLessonsEntityId
import com.melowetty.hsepermhelper.persistence.projection.GroupByTimetableIdProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GroupLessonsRepository : JpaRepository<GroupLessonsEntity, GroupLessonsEntityId> {
    fun findAllById_TimetableIdIn(timetableIds: List<String>): List<GroupLessonsEntity>
    fun findAllById_TimetableId(timetableId: String): List<GroupLessonsEntity>
    fun findById_TimetableIdAndId_Group(timetableId: String, group: String): GroupLessonsEntity?

    @Modifying
    fun deleteById_TimetableId(timetableId: String)

    @Query("DELETE FROM group_lessons WHERE timetable_id IN (:timetableIds)", nativeQuery = true)
    @Modifying
    fun deleteGroupsLessonsByTimetableIds(timetableIds: List<String>)

    @Query("SELECT DISTINCT new com.melowetty.hsepermhelper.persistence.projection.GroupByTimetableIdProjection(id.group, id.timetableId) " +
            "FROM GroupLessonsEntity WHERE id.timetableId IN :timetableIds")
    fun getGroupsAndTimetableIds(timetableIds: List<String>): List<GroupByTimetableIdProjection>
}