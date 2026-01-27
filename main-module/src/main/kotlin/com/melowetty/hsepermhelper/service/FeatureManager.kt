package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.config.FeatureConfig
import com.melowetty.hsepermhelper.domain.model.Feature
import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import org.springframework.stereotype.Service

// TODO: вынести в отдельный модуль
@Service
class FeatureManager(
    private val config: FeatureConfig,
) {
    fun isEnabled(feature: Feature): Boolean = getFeature(feature).enabled

    fun isEnabledForUser(feature: Feature, user: UserRecord): Boolean {
        val enabledFor = getFeature(feature).enabledFor.toSet()
        val isEnabledByRole = user.roles.any { it in enabledFor }
        return isEnabled(feature) || isEnabledByRole
    }

    private fun getFeature(feature: Feature) = when (feature) {
        Feature.NEW_CHANGED_TIMETABLE_NOTIFICATION -> config.newChangedTimetableNotification
        Feature.NEW_ADDED_TIMETABLES_NOTIFICATION -> config.addedTimetablesNotification
    }
}