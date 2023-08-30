package com.melowetty.hsepermhelper.models

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ссылки для скачивания календаря")
data class ScheduleFileLinks(
    @Schema(description = "Ссылка на прямое скачивание файла для импорта в календарь")
    val linkForDownload: String,
    @Schema(description = "Ссылка на добавление удаленного календаря")
    val linkForRemoteCalendar: String,
)
