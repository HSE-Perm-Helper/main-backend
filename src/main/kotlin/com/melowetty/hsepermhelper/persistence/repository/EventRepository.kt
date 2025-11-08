package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.persistence.entity.EventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<EventEntity, Long>