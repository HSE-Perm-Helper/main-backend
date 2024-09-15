package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.domain.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<UserEntity, UUID> {
    fun findAllBySettings_EnabledComingLessonsNotifications(enabledComingNotifications: Boolean): List<UserEntity>
    fun findByTelegramId(telegramId: Long): Optional<UserEntity>
    fun existsByTelegramId(telegramId: Long): Boolean
    fun findAllBySettingsGroupAndSettingsSubGroup(group: String, subGroup: Int): List<UserEntity>
}