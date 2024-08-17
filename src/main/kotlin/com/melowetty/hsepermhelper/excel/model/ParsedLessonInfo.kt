package com.melowetty.hsepermhelper.excel.model

data class ParsedLessonInfo(
    val isSessionWeek: Boolean,
    val subject: String,
    val lessonInfo: String? = null,
    val additionalInfo: List<String>? = null,
    val isUnderlined: Boolean,
    val isHaveBuildingInfo: Boolean,
)
