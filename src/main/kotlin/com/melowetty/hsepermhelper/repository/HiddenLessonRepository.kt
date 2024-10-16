package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.domain.entity.HideLessonEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HiddenLessonRepository : JpaRepository<HideLessonEntity, Long>