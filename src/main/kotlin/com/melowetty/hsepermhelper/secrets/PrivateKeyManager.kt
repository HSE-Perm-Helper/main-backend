package com.melowetty.hsepermhelper.secrets

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component


@Component
class PrivateKeyManager(
    private val env: Environment
) {
    private val PRIVATE_KEY = getPrivateKey()
    fun checkKey(key: String?): Boolean {
        if (key == null) return false
        return PRIVATE_KEY == key
    }

    private fun getPrivateKey(): String {
        return env.getProperty("app.security.private-key") ?: ""
    }
}