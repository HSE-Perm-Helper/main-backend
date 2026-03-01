package com.melowetty.hsepermhelper.domain.model.timetable

enum class EducationType(
    val educationLevel: EducationLevel,
) {
    BACHELOR_OFFLINE(EducationLevel.BACHELOR),
    BACHELOR_ONLINE(EducationLevel.BACHELOR),
    MASTER(EducationLevel.MASTER);
}