package com.melowetty.hsepermhelper.context

import com.melowetty.hsepermhelper.domain.model.context.JobRunContext

object JobRunContextHolder {
    private val holder: ThreadLocal<JobRunContext?> = ThreadLocal.withInitial {
        null
    }

    fun get(): JobRunContext? {
        return holder.get()
    }

    fun set(context: JobRunContext) {
        holder.set(context)
    }

    fun clear() {
        holder.remove()
    }
}