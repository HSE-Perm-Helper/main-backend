package com.melowetty.hsepermhelper.controllers

import com.melowetty.hsepermhelper.controllers.request.NotificationData
import com.melowetty.hsepermhelper.models.Response
import com.melowetty.hsepermhelper.notification.Notification
import com.melowetty.hsepermhelper.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = "Уведомления", description = "Взаимодействие с уведомлениями")
@RestController
class NotificationController(
    private val notificationService: NotificationService,
) {
    @Operation(
        summary = "Получить список ивентов",
        description = "Позволяет получить списков всех ивентов"
    )
    @GetMapping(
        "events",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getEvents(): Response<List<Notification>> {
        return Response(notificationService.getAllNotifications())
    }


    @Operation(
        summary = "Удаление ивентов",
        description = "Позволяет удалить указанные в теле запроса ивенты",
    )
    @DeleteMapping(
        "events",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    fun deleteEvents(
        @RequestBody
        @Parameter(description = "Список ивентов для удаления")
        events: List<NotificationData>
    ) {
        notificationService.deleteNotifications(events)
    }

    @Operation(
        summary = "Получить список уведомлений",
        description = "Позволяет получить списков всех уведомлений"
    )
    @GetMapping(
        "notifications",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getNotifications(): Response<List<Notification>> {
        return Response(notificationService.getAllNotifications())
    }


    @Operation(
        summary = "Удаление уведомлений",
        description = "Позволяет удалить указанные в теле запроса уведомления",
    )
    @DeleteMapping(
        "notifications",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    fun deleteNotifications(
        @RequestBody
        @Parameter(description = "Список уведомлений для удаления")
        notifications: List<NotificationData>
    ) {
        notificationService.deleteNotifications(notifications)
    }
}