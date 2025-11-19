package com.melowetty.hsepermhelper.controller.user.v3

import com.melowetty.hsepermhelper.domain.dto.RemoteScheduleLink
import com.melowetty.hsepermhelper.service.user.UserRemoteTimetableService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v3/users/{id}/remote-timetable")
class UserRemoteTimetableController(
    private val userRemoteTimetableService: UserRemoteTimetableService,
) {
    @GetMapping
    fun getRemoteTimetableLink(@PathVariable("id") userId: UUID): RemoteScheduleLink {
        return userRemoteTimetableService.getRemoteTimetableLink(userId)
    }

    @PostMapping
    fun createOrUpdateTimetableLink(@PathVariable("id") userId: UUID): RemoteScheduleLink {
        return userRemoteTimetableService.createOrUpdateTimetableLink(userId)
    }
}