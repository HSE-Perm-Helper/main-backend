package com.melowetty.hsepermhelper.service.user

import com.melowetty.hsepermhelper.domain.dto.RemoteScheduleLink
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.persistence.storage.UserStorage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@Service
class UserRemoteTimetableService(
    private val userStorage: UserStorage,
//    private val remoteScheduleService: RemoteScheduleService,
) {
    @Value("\${remote-schedule.connect-url}")
    private lateinit var remoteTimetableConnectUrl: String

    fun getRemoteTimetableLink(id: UUID): RemoteScheduleLink {
        val user = userStorage.findUserById(id)
            ?: throw UserByIdNotFoundException(id)

        try {
            val token = "123"

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

        val token = "123"

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