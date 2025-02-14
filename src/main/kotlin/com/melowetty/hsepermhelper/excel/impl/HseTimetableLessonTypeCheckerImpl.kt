package com.melowetty.hsepermhelper.excel.impl

import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.excel.HseTimetableLessonTypeChecker
import com.melowetty.hsepermhelper.excel.model.ParsedLessonInfo
import java.time.Month
import org.springframework.stereotype.Component

@Component
class HseTimetableLessonTypeCheckerImpl : HseTimetableLessonTypeChecker {
    override fun getLessonType(lessonInfo: ParsedLessonInfo): LessonType {
        val pureSubject = lessonInfo.subject.lowercase()
        val pureLessonInfo = lessonInfo.lessonInfo?.lowercase()
        val pureFullLessonInfo = pureSubject + " " +
                (lessonInfo.additionalInfo?.joinToString { it.lowercase() } ?: "") + " " +
                (pureLessonInfo ?: "")
        if (pureFullLessonInfo.contains("ведомост")) return LessonType.STATEMENT
        if (pureFullLessonInfo.contains("независимый экзамен")) return LessonType.INDEPENDENT_EXAM
        if (pureFullLessonInfo.contains("консультация")) return LessonType.CONSULT
        if (pureFullLessonInfo.contains("экзамен")) return LessonType.EXAM
        if (pureFullLessonInfo.contains("зачёт") || pureSubject.contains("зачет")) return LessonType.TEST
        if (pureFullLessonInfo.contains("английский язык")) return LessonType.COMMON_ENGLISH
        if (pureFullLessonInfo.contains("майнор")) {
            val scheduleStart = lessonInfo.schedulePeriod.start

            val isOctober = scheduleStart.month == Month.OCTOBER
            val isMarch = scheduleStart.month == Month.MARCH

            if (lessonInfo.isSessionWeek && !isMarch && !isOctober) return LessonType.EXAM
            return LessonType.COMMON_MINOR
        }
        if (pureSubject == "практика") return LessonType.PRACTICE
        if (pureFullLessonInfo.contains("лекция") || pureSubject.contains("лекции")) return LessonType.LECTURE
        if (pureFullLessonInfo.contains("лек.")) return LessonType.LECTURE
        if (pureFullLessonInfo.contains("сем.") || pureFullLessonInfo.contains("практ.")) return LessonType.SEMINAR
        if (pureFullLessonInfo.contains("семинар") || pureSubject.contains("семинары")) return LessonType.SEMINAR
        if (pureFullLessonInfo.contains("доц по выбору")) return LessonType.UNDEFINED_AED
        if (pureFullLessonInfo.contains("доц")) return LessonType.AED
        if (lessonInfo.isHaveBuildingInfo.not()) return LessonType.EVENT
        if (lessonInfo.isUnderlined) return LessonType.LECTURE
        return LessonType.SEMINAR
    }
}