package com.melowetty.hsepermhelper.util

class LinkUtils {
    companion object {
        const val LINK_REGEX_PATTERN = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"
        val LINK_REGEX = Regex(LINK_REGEX_PATTERN)
    }
}