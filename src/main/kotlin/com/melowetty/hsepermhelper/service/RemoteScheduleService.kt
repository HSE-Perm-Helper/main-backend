package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.domain.model.RemoteScheduleToken
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient("remote-schedule-service", url = "\${api.remote-schedule-service.url:}")
interface RemoteScheduleService {
    @GetMapping("/remote-schedule-management")
    fun getUserScheduleToken(
        @RequestParam("telegramId") telegramId: Long,
    ): RemoteScheduleToken

    @PostMapping("/remote-schedule-management")
    fun createOrUpdateUserScheduleToken(
        @RequestParam("telegramId") telegramId: Long,
    ): RemoteScheduleToken
}