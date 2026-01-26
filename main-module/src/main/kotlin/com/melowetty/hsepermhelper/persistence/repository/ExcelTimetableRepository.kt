package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.persistence.entity.ExcelTimetableEntity
import com.melowetty.hsepermhelper.timetable.model.EducationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ExcelTimetableRepository : JpaRepository<ExcelTimetableEntity, String> {
    @Query("SELECT e FROM ExcelTimetableEntity e WHERE e.educationType = :educationType AND e.isVisible = true AND e.isParent = true")
    fun findAllByVisibleIsTrueAndParentIsTrue(educationType: EducationType): List<ExcelTimetableEntity>

    @Query("UPDATE ExcelTimetableEntity SET isVisible = true WHERE id IN (:ids)")
    @Modifying
    fun setVisibleByIds(ids: List<String>)

    @Query("UPDATE ExcelTimetableEntity SET isVisible = false WHERE id IN (:ids)")
    @Modifying
    fun setHiddenByIds(ids: List<String>)

    @Query("UPDATE ExcelTimetableEntity SET runId = :runId, updated = CURRENT_TIMESTAMP WHERE id IN (:ids)")
    @Modifying
    fun setRunIdByIds(ids: List<String>, runId: String)

    @Query("UPDATE ExcelTimetableEntity SET runId = :runId, updated = CURRENT_TIMESTAMP WHERE runId = :oldRunId")
    @Modifying
    fun setRunIdByOldRunId(oldRunId: String, runId: String)

    fun findAllByRunId(runId: String): List<ExcelTimetableEntity>

    @Modifying
    fun deleteAllByRunId(runId: String)
}