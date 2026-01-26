package com.melowetty.hsepermhelper.persistence.entity

import com.melowetty.hsepermhelper.domain.model.user.EducationGroupEntity
import com.melowetty.hsepermhelper.domain.model.user.UserRole
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "telegram_id", unique = true)
    val telegramId: Long = 0L,

    @Column(name = "email")
    var email: String? = null,

    @Embedded
    var educationGroup: EducationGroupEntity,

    @Column(name = "is_enabled_new_schedule_notification")
    var isEnabledNewScheduleNotifications: Boolean,

    @Column(name = "is_enabled_changed_schedule_notification")
    var isEnabledChangedScheduleNotifications: Boolean,

    @Column(name = "is_enabled_coming_lessons_notification")
    var isEnabledComingLessonsNotifications: Boolean,

    @CreatedDate
    val createdDate: LocalDateTime = LocalDateTime.now(),

    @CollectionTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")]
    )
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    val roles: List<UserRole>,
) {
    fun id(): UUID {
        return id ?: throw IllegalStateException("User entity id is null.")
    }
}