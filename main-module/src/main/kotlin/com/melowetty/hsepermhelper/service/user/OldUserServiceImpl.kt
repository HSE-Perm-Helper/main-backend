package com.melowetty.hsepermhelper.service.user

import com.melowetty.hsepermhelper.domain.dto.UserDto
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.toDto
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import com.melowetty.hsepermhelper.service.OldUserService
import com.melowetty.hsepermhelper.util.Paginator
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Deprecated("Migrate to new user service")
class OldUserServiceImpl(
    private val userStorage: UserStorage,
) : OldUserService {
    override fun getAllUsers(): List<UserDto> {
        val result = mutableListOf<UserDto>()
        Paginator.fetchPageable(
            fetchFunction = { limit, token ->
                val options = UserStorage.Options(
                    withRoles = true,
                    withHiddenLessons = true
                )
                userStorage.findUsersAfterId(token, limit, options = options)
            }
        ) { users ->
            result.addAll(users.map { it.toDto() })
        }

        return result
    }
}