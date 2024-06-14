package com.melowetty.hsepermhelper.security

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component


@Component
class PrivateKeyCheckManager(
    private val env: Environment
) {
    private val privateKey = env.getProperty("app.security.private-key") ?: ""

    fun isEnabled(): Boolean {
        return privateKey.isNotBlank()
    }

    fun checkKey(key: String?): Boolean {
        if(privateKey.isBlank()) return true;
        if (key == null) return false
        return privateKey == key
    }
}