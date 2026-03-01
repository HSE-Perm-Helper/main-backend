package com.melowetty.hsepermhelper.remote.service

import com.melowetty.hsepermhelper.domain.dto.RemoteScheduleLink
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import java.util.UUID
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class UserRemoteTimetableService(
    private val userStorage: UserStorage,
    private val remoteTimetableManagementService: RemoteTimetableManagementService
) {
    @Value("\${remote-schedule.connect-url}")
    private lateinit var remoteTimetableConnectUrl: String

    fun getRemoteTimetableLink(id: UUID): RemoteScheduleLink {
        val user = userStorage.findUserById(id)
            ?: throw UserByIdNotFoundException(id)

        try {
            val token = remoteTimetableManagementService.getToken(user.id)

            return RemoteScheduleLink(
                direct = generateRemoteScheduleConnectLink(token)
            )
        } catch (e: RuntimeException) {
            return createOrUpdateTimetableLink(id)
        }
    }

    fun createOrUpdateTimetableLink(id: UUID): RemoteScheduleLink {
        val user = userStorage.findUserById(id)
            ?: throw UserByIdNotFoundException(id)

        val token = remoteTimetableManagementService.createOrUpdateToken(user.id)

        return RemoteScheduleLink(
            direct = generateRemoteScheduleConnectLink(token)
        )
    }

    private fun generateRemoteScheduleConnectLink(token: String): String {
        return UriComponentsBuilder.fromUriString(remoteTimetableConnectUrl)
            .queryParam("token", token)
            .encode()
            .toUriString()
    }
}