package com.melowetty.hsepermhelper.controller

import com.melowetty.hsepermhelper.domain.dto.EmailVerificationDto
import com.melowetty.hsepermhelper.service.EmailVerificationService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("verification")
class EmailVerificationController(
    private val emailVerificationService: EmailVerificationService
) {
    @GetMapping("verify/{secret}")
    fun verifyEmailBySecret(@PathVariable("secret") secret: String): String {
        emailVerificationService.checkVerificationSecret(secret)
        return "Почта успешно подтверждена, эту страницу можно закрыть"
    }

    @GetMapping("{token}")
    fun getEmailVerificationInfo(@PathVariable("token") token: String): EmailVerificationDto {
        return emailVerificationService.getVerificationByToken(token)
    }

    @PostMapping("{token}")
    fun resendVerificationMessage(@PathVariable("token") token: String): EmailVerificationDto {
        return emailVerificationService.resendVerificationLink(token)
    }

    @DeleteMapping("{token}")
    fun cancelVerification(@PathVariable("token") token: String) {
        return emailVerificationService.cancelVerification(token)
    }
}