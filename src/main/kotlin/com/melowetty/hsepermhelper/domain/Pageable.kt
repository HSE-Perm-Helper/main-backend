package com.melowetty.hsepermhelper.domain

import java.util.UUID

data class Pageable<T>(
    val data: List<T>,
    val nextId: UUID?,
)
