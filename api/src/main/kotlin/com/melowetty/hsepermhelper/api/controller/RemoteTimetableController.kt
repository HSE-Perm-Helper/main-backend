package com.melowetty.hsepermhelper.api.controller

import com.melowetty.hsepermhelper.remote.service.RemoteTimetableService
import java.nio.charset.Charset
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("remote-schedule")
class RemoteTimetableController(
    private val remoteTimetableService: RemoteTimetableService,
) {
    @GetMapping
    fun getRemoteSchedule(
        @RequestParam token: String
    ): ResponseEntity<ByteArray> {
        return ResponseEntity.ok()
            .contentType(MediaType.asMediaType(MimeType("text", "calendar", Charset.forName("UTF-8"))))
            .body(remoteTimetableService.getRemoteScheduleAsText(token).toByteArray())
    }

    @GetMapping("/redirect")
    fun redirect(@RequestParam target: String): RedirectView {
        return RedirectView(target)
    }
}