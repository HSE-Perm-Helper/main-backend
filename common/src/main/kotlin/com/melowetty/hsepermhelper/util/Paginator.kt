package com.melowetty.hsepermhelper.util

import com.melowetty.hsepermhelper.domain.Pageable
import java.util.UUID

object Paginator {
    fun <T> fetchPageable(
        limit: Int = 500,
        fetchFunction: (limit: Int, token: UUID?) -> Pageable<T>,
        processFunction: (List<T>) -> Unit,
    ) {
        var token: UUID? = null

        while (true) {
            val page = fetchFunction(limit, token)
            processFunction(page.data)

            if (page.nextId == null) {
                break
            }

            token = page.nextId
        }
    }
}