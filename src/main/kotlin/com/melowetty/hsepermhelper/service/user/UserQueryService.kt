package com.melowetty.hsepermhelper.service.user

import com.melowetty.hsepermhelper.domain.Pageable
import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toDto
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import com.melowetty.hsepermhelper.timetable.model.EducationType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserQueryService(
    private val userStorage: UserStorage,
) {
    @Transactional(readOnly = true)
    fun findUsersAfterId(
        lastId: UUID?,
        size: Int = 500,
        educationType: EducationType? = null,
        isEnabledNewSchedule: Boolean? = null,
        isEnabledChangedSchedule: Boolean? = null,
        isEnabledComingLessons: Boolean? = null,
    ): Pageable<UserDto> {
        return userStorage.findUsersAfterId(
            lastId = lastId,
            size = size,
            educationType = educationType,
            isEnabledNewSchedule = isEnabledNewSchedule,
            isEnabledChangedSchedule = isEnabledChangedSchedule,
            isEnabledComingLessons = isEnabledComingLessons,
            options = UserStorage.Options(
                withRoles = true,
                withHiddenLessons = true
            )
        ).let {
            Pageable(
                it.data.map { it.toDto() },
                it.nextId,
            )
        }
    }
}