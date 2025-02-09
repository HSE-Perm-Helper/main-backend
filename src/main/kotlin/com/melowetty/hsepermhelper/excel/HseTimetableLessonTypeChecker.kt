package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.excel.model.ParsedLessonInfo

interface HseTimetableLessonTypeChecker {
    fun getLessonType(lessonInfo: ParsedLessonInfo): LessonType
}