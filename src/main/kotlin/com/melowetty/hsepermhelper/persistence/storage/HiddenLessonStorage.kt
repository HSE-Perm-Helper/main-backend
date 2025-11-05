package com.melowetty.hsepermhelper.persistence.storage

import com.melowetty.hsepermhelper.persistence.entity.HideLessonEntity
import com.melowetty.hsepermhelper.persistence.entity.HideLessonId
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.persistence.projection.HideLessonRecord
import com.melowetty.hsepermhelper.persistence.repository.HiddenLessonRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class HiddenLessonStorage(
    private val hiddenLessonRepository: HiddenLessonRepository
) {
    fun getUsersHiddenLessons(userIds: List<UUID>): Map<UUID, List<HideLessonRecord>> {
        return hiddenLessonRepository.getById_UserIdIn(userIds).map { it.toRecord() }.groupBy { it.userId }
    }

    fun getUserHiddenLessons(userId: UUID): List<HideLessonRecord> {
        return hiddenLessonRepository.getById_UserId(userId).map { it.toRecord() }
    }

    fun hideLesson(userId: UUID, lesson: String, lessonType: LessonType, subGroup: Int?) {
        val id = HideLessonId(userId, lesson, lessonType, subGroup)

        if (hiddenLessonRepository.existsById(id)) {
            logger.warn { "User $userId already has hidden lesson $lesson" }
            return
        }
        val hiddenLesson = HideLessonEntity(id)
        hiddenLessonRepository.save(hiddenLesson)

        logger.info { "User $userId hides lesson $lesson" }
    }

    fun unHideLesson(userId: UUID, lesson: String, lessonType: LessonType, subGroup: Int?) {
        val id = HideLessonId(userId, lesson, lessonType, subGroup)

        if (!hiddenLessonRepository.existsById(id)) {
            logger.warn { "User $userId has no hidden lesson $lesson" }
            return
        }

        hiddenLessonRepository.deleteById(id)

        logger.info { "User $userId un hides lesson $lesson" }
    }

    fun clearHiddenLessons(userId: UUID) {
        logger.info { "Clearing hidden lessons for user $userId" }
        hiddenLessonRepository.deleteById_UserId(userId)
    }

    private fun HideLessonEntity.toRecord() = HideLessonRecord(
        userId = id.userId,
        lesson = id.lesson,
        lessonType = id.lessonType,
        subGroup = id.subGroup
    )

    companion object {
        private val logger = KotlinLogging.logger {  }
    }
}