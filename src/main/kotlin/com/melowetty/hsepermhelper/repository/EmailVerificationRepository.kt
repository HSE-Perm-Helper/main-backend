package com.melowetty.hsepermhelper.repository

import com.melowetty.hsepermhelper.domain.entity.EmailVerificationEntity
import com.melowetty.hsepermhelper.domain.entity.UserEntity
import java.time.LocalDateTime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface EmailVerificationRepository: JpaRepository<EmailVerificationEntity, String> {
    fun findByToken(token: String): EmailVerificationEntity?
    fun findBySecret(secret: String): EmailVerificationEntity?
    fun findByUser(user: UserEntity): EmailVerificationEntity?

    @Modifying
    @Transactional
    fun deleteByCreatedLessThanEqual(created: LocalDateTime)
}