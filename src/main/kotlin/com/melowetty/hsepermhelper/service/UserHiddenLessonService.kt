package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.persistence.projection.HideLessonRecord
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.persistence.repository.UserRepository
import com.melowetty.hsepermhelper.persistence.storage.HiddenLessonStorage
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserHiddenLessonService(
    private val userRepository: UserRepository,
    private val hiddenLessonStorage: HiddenLessonStorage,
) {
    fun getUsersHiddenLessons(userIds: List<UUID>): Map<UUID, List<HideLessonRecord>>  {
        return hiddenLessonStorage.getUsersHiddenLessons(userIds)
    }

    fun getUserHiddenLessons(userId: UUID): List<HideLessonRecord> {
        validateUser(userId)
        return hiddenLessonStorage.getUserHiddenLessons(userId)
    }

    fun hideLesson(userId: UUID, lesson: String, lessonType: LessonType, subGroup: Int?) {
        validateUser(userId)
        hiddenLessonStorage.hideLesson(userId, lesson, lessonType, subGroup)
    }

    fun unHideLesson(userId: UUID, lesson: String, lessonType: LessonType, subGroup: Int?) {
        validateUser(userId)
        hiddenLessonStorage.unHideLesson(userId, lesson, lessonType, subGroup)
    }

    fun clearHiddenLessons(userId: UUID) {
        validateUser(userId)
        hiddenLessonStorage.clearHiddenLessons(userId)
    }

    // TODO: use storage
    private fun validateUser(userId: UUID) {
        if (!userRepository.existsById(userId)) {
            throw UserByIdNotFoundException(userId)
        }
    }
}