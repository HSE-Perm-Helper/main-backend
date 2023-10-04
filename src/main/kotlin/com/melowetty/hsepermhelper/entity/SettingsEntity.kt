package com.melowetty.hsepermhelper.entity

import jakarta.persistence.*

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

    @Column(name = "include_quarter_schedule")
    val includeQuarterSchedule: Boolean? = false,

    @Column(name = "include_common_english")
    val includeCommonEnglish: Boolean? = false,

    @Column(name = "include_common_minor", columnDefinition="BOOLEAN DEFAULT true")
    val includeCommonMinor: Boolean? = true,

    @Column(name = "is_enabled_remote_calendar")
    val isEnabledRemoteCalendar: Boolean? = false,

    @Column(name = "is_enabled_new_common_schedule_notification", columnDefinition="BOOLEAN DEFAULT true")
    val isEnabledNewCommonScheduleNotifications: Boolean? = true,

    @Column(name = "is_enabled_new_quarter_schedule_notification")
    val isEnabledNewQuarterScheduleNotifications: Boolean? = false,
)