package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.domain.model.user.UserRole
import com.melowetty.hsepermhelper.persistence.entity.UserEntity
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {
    fun findAllByIsEnabledComingLessonsNotifications(enabledComingNotifications: Boolean): List<UserEntity>

    @Query("SELECT u.id FROM UserEntity u WHERE u.telegramId = :telegramId")
    fun getIdByTelegramId(telegramId: Long): UUID?
    fun existsByTelegramId(telegramId: Long): Boolean
    fun findByEmailNotNull(pageable: Pageable): Page<UserEntity>
    fun existsByEmail(email: String): Boolean
    fun findAllByRolesContains(role: UserRole): List<UserEntity>
}