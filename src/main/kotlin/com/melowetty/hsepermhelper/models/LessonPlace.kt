package com.melowetty.hsepermhelper.models

import io.swagger.v3.oas.annotations.media.Schema

data class LessonPlace(
    @Schema(description = "Кабинет", example = "121", nullable = true)
    val office: String?,
    @Schema(description = "Корпус (если 0 - пара дистанционная)", example = "2", nullable = true)
    val building: Int?,
)