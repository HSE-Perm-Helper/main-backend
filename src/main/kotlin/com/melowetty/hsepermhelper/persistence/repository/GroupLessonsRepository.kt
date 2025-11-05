package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.persistence.entity.GroupLessonsEntity
import com.melowetty.hsepermhelper.persistence.entity.GroupLessonsEntityId
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

    @Query("SELECT DISTINCT group FROM group_lessons WHERE timetable_id IN (:timetableIds) ORDER BY group ASC", nativeQuery = true)
    fun getTimetablesGroups(timetableIds: List<String>): List<String>

    @Query("UPDATE group_lessons SET timetable_id = :newTimetableId WHERE timetable_id IN (:oldTimetableId)", nativeQuery = true)
    @Modifying
    fun updateTimetableId(oldTimetableId: List<String>, newTimetableId: String)
}