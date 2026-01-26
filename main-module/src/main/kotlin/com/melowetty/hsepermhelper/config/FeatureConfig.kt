package com.melowetty.hsepermhelper.config

import com.melowetty.hsepermhelper.domain.model.user.UserRole
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.features")
class FeatureConfig {
    lateinit var newChangedTimetableNotification: Feature
    lateinit var addedTimetablesNotification: Feature
}

data class Feature(
    val enabled: Boolean = false,
    val enabledFor: MutableList<UserRole> = mutableListOf()
)