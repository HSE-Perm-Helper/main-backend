package com.melowetty.hsepermhelper.domain.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "settings")
data class SettingsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "email")
    val email: String? = null,

    @Column(name = "user_group")
    val group: String = "",

    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val hiddenLessons: Set<HideLessonEntity>,

    @Column(name = "is_enabled_new_schedule_notification", columnDefinition = "BOOLEAN DEFAULT true")
    val isEnabledNewScheduleNotifications: Boolean = true,

    @Column(name = "is_enabled_changed_schedule_notification", columnDefinition = "BOOLEAN DEFAULT true")
    val isEnabledChangedScheduleNotifications: Boolean = true,

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    val isEnabledComingLessonsNotifications: Boolean = false,
)