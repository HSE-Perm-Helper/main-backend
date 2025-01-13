package com.melowetty.hsepermhelper.fallback

import com.melowetty.hsepermhelper.model.RemoteScheduleToken
import com.melowetty.hsepermhelper.service.RemoteScheduleService
import jakarta.ws.rs.NotFoundException
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component

@Component
class RemoteScheduleServiceFallback: FallbackFactory<RemoteScheduleService> {
    override fun create(cause: Throwable?): RemoteScheduleService {
        return object : RemoteScheduleService {
            override fun getUserScheduleToken(telegramId: Long): RemoteScheduleToken? {
                return null
            }

            override fun createOrUpdateUserScheduleToken(telegramId: Long): RemoteScheduleToken {
                throw cause!!
            }

        }
    }

}