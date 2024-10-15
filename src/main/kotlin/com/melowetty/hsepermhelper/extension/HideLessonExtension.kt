package com.melowetty.hsepermhelper.extension

import com.melowetty.hsepermhelper.domain.dto.HideLessonDto
import com.melowetty.hsepermhelper.domain.entity.HideLessonEntity

class HideLessonExtension {
    companion object {
        fun HideLessonEntity.toDto(): HideLessonDto {
            return HideLessonDto(
                lesson = lesson,
                lessonType = lessonType,
                subGroup = subGroup,
            )
        }
    }
}