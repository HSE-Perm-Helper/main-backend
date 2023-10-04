package com.melowetty.hsepermhelper.controllers

import com.melowetty.hsepermhelper.events.common.PublicEvent
import com.melowetty.hsepermhelper.events.common.PublicEventDto
import com.melowetty.hsepermhelper.models.Response
import com.melowetty.hsepermhelper.service.EventService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = "Ивенты", description = "Взаимодействие с ивентами")
@RestController
class EventController(
    private val eventService: EventService,
) {
    @Operation(
        summary = "Получить список ивентов",
        description = "Позволяет получить списков всех ивентов"
    )
    @GetMapping(
        "events",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getEvents(): Response<List<PublicEvent>> {
        return Response(eventService.getAllEvents())
    }

    @Operation(
        summary = "Удаление ивентов",
        description = "Позволяет удалить один или все ивенты из стэка (нужно передать параметр clear=true, " +
                "чтобы очистить стэк ивентов"
    )
    @DeleteMapping(
        "events",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    fun deleteEvents(
        @Parameter(description = "Очистить ли список ивентов")
        @RequestParam clear: Boolean?
    ) {
        if(clear != null) {
            eventService.clearEvents()
        } else {
            eventService.deleteFirstEvent()
        }
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
        events: List<PublicEventDto>
    ) {
        eventService.deleteEvents(events)
    }
}