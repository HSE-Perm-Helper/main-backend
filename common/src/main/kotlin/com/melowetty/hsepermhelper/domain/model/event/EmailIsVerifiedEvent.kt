package com.melowetty.hsepermhelper.domain.model.event

import java.util.UUID

data class EmailIsVerifiedEvent(
    val userId: UUID,
    val email: String
)
