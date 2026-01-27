package com.melowetty.hsepermhelper.api.controller

import com.melowetty.hsepermhelper.domain.dto.EmailVerificationDto
import com.melowetty.hsepermhelper.service.EmailVerificationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("verification")
class EmailVerificationController(
    private val emailVerificationService: EmailVerificationService
) {
    @Value("\${email-verification.redirect-url}")
    private lateinit var emailVerificationRedirectUrl: String

    @GetMapping("verify/{secret}")
    fun verifyEmailBySecret(@PathVariable("secret") secret: String): RedirectView {
        emailVerificationService.checkVerificationSecret(secret)
        return RedirectView(emailVerificationRedirectUrl)
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