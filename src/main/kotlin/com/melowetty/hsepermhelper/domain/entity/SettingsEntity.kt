package com.melowetty.hsepermhelper.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "settings")
data class SettingsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "user_group")
    val group: String = "",

    @Column(name = "user_sub_group")
    val subGroup: Int = 0,

    @Column(name = "include_common_english", columnDefinition = "BOOLEAN DEFAULT false")
    val includeCommonEnglish: Boolean = false,

    @Column(name = "include_common_minor", columnDefinition = "BOOLEAN DEFAULT true")
    val includeCommonMinor: Boolean = true,

    @Column(name = "is_enabled_new_schedule_notification", columnDefinition = "BOOLEAN DEFAULT true")
    val isEnabledNewScheduleNotifications: Boolean = true,

    @Column(name = "is_enabled_changed_schedule_notification", columnDefinition = "BOOLEAN DEFAULT true")
    val isEnabledChangedScheduleNotifications: Boolean = true,

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    val isEnabledComingLessonsNotifications: Boolean = false,
)