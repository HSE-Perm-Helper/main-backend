package com.melowetty.hsepermhelper.timetable.model

enum class EducationType(
    val educationLevel: EducationLevel,
) {
    BACHELOR_OFFLINE(EducationLevel.BACHELOR),
    BACHELOR_ONLINE(EducationLevel.BACHELOR),
    MASTER(EducationLevel.MASTER);
}