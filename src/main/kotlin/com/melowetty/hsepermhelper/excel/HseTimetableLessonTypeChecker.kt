package com.melowetty.hsepermhelper.excel

import com.melowetty.hsepermhelper.excel.model.ParsedLessonInfo
import com.melowetty.hsepermhelper.model.lesson.LessonType

interface HseTimetableLessonTypeChecker {
    fun getLessonType(lessonInfo: ParsedLessonInfo): LessonType
}