package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.domain.model.user.UserRole
import com.melowetty.hsepermhelper.persistence.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {
    fun findAllByIsEnabledComingLessonsNotifications(enabledComingNotifications: Boolean): List<UserEntity>

    @Query("SELECT u.id FROM UserEntity u WHERE u.telegramId = :telegramId")
    fun getIdByTelegramId(telegramId: Long): UUID?

    fun getById(id: UUID): UserEntity?

    fun findByTelegramId(telegramId: Long): Optional<UserEntity>
    fun existsByTelegramId(telegramId: Long): Boolean
    fun findByEmailNotNull(pageable: Pageable): Page<UserEntity>
    fun existsByEmail(email: String): Boolean
    fun findAllByRolesContains(role: UserRole): List<UserEntity>
}