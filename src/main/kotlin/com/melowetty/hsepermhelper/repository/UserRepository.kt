package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.domain.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@Repository
interface UserRepository : CrudRepository<UserEntity, UUID> {
    fun findAllBySettings_IsEnabledComingLessonsNotifications(enabledComingNotifications: Boolean): List<UserEntity>
    fun findByTelegramId(telegramId: Long): Optional<UserEntity>
    fun existsByTelegramId(telegramId: Long): Boolean


    fun findBySettings_EmailNotNull(pageable: Pageable): Page<UserEntity>
}