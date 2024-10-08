package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.domain.entity.UserEntity
import com.melowetty.hsepermhelper.domain.entity.UserEventEntity
import com.melowetty.hsepermhelper.model.UserEventType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserEventRepository : CrudRepository<UserEventEntity, Long> {
    fun findByTargetUser(user: UserEntity): List<UserEventEntity>
    fun findByTargetUserAndUserEventType(user: UserEntity, eventType: UserEventType): List<UserEventEntity>
}