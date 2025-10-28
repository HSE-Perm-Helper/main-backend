package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.domain.entity.UserEntity
import com.melowetty.hsepermhelper.domain.model.user.UserRole
import java.util.Optional
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<UserEntity, UUID> {
    fun findAllBySettings_IsEnabledComingLessonsNotifications(enabledComingNotifications: Boolean): List<UserEntity>

    @Query("SELECT u.id FROM UserEntity u WHERE u.telegramId = :telegramId")
    fun getIdByTelegramId(telegramId: Long): UUID?

    fun getById(id: UUID): UserEntity?

    fun findByTelegramId(telegramId: Long): Optional<UserEntity>
    fun existsByTelegramId(telegramId: Long): Boolean
    fun findByEmailNotNull(pageable: Pageable): Page<UserEntity>
    fun existsByEmail(email: String): Boolean
    fun findAllByRolesContains(role: UserRole): List<UserEntity>

    @Query("""
        SELECT u
        FROM UserEntity u
        LEFT JOIN FETCH u.settings us
        WHERE ((:cursorId IS NULL OR u.id > :cursorId))
        ORDER BY u.id ASC
        LIMIT :limit
    """)
    fun findAllWithPagingByEducationType(
        @Param("cursorId") cursorId: UUID?,
        limit: Int
    ): List<UserEntity>
}