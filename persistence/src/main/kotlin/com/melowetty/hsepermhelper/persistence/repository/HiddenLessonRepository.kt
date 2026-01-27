package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.persistence.entity.HideLessonEntity
import com.melowetty.hsepermhelper.persistence.entity.HideLessonId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface HiddenLessonRepository : JpaRepository<HideLessonEntity, HideLessonId> {
    fun getById_UserIdIn(userId: List<UUID>): List<HideLessonEntity>
    fun getById_UserId(userId: UUID): List<HideLessonEntity>
    fun deleteById_UserId(userId: UUID)
}