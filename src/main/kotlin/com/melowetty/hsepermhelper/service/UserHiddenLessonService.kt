package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.lesson.AvailableLessonForHiding
import com.melowetty.hsepermhelper.persistence.projection.HideLessonRecord
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.persistence.repository.UserRepository
import com.melowetty.hsepermhelper.persistence.storage.HiddenLessonStorage
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserHiddenLessonService(
    private val userStorage: UserStorage,
    private val hiddenLessonStorage: HiddenLessonStorage,
    private val personalTimetableService: PersonalTimetableService
) {
   fun getLessonsForHiding(userId: UUID): List<AvailableLessonForHiding> {
       return personalTimetableService.getLessonsForHiding(userId)
   }

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

    private fun validateUser(userId: UUID) {
        if (!userStorage.existsUserById(userId)) {
            throw UserByIdNotFoundException(userId)
        }
    }
}