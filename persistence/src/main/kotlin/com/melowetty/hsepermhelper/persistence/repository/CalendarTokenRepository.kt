package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.persistence.entity.CalendarTokenEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalendarTokenRepository : JpaRepository<CalendarTokenEntity, UUID> {
    fun findByToken(token: String): CalendarTokenEntity?
}