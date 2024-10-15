package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.domain.entity.HideLessonEntity
import com.melowetty.hsepermhelper.domain.entity.SettingsEntity
import com.melowetty.hsepermhelper.domain.entity.UserEntity
import com.melowetty.hsepermhelper.extension.UserExtensions.Companion.getGroupedEntityBySettingsUsers
import com.melowetty.hsepermhelper.model.LessonType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UserExtensionsTest {
    @Test
    fun `test group users entity by settings`() {
        val user = UserEntity(
            settings = SettingsEntity(
                group = "РИС-22-3",
                subGroup = 1,
                hiddenLessons = setOf(HideLessonEntity(id = 0, lesson = "Test", lessonType = LessonType.TEST, 2))
            )
        )

        val users = listOf(user, user.copy(settings = user.settings.copy(subGroup = 2)), user.copy())

        val groupedUsers = users.getGroupedEntityBySettingsUsers()

        Assertions.assertEquals(2, groupedUsers.size)
        Assertions.assertEquals(3, groupedUsers.values.flatten().size)
    }
}