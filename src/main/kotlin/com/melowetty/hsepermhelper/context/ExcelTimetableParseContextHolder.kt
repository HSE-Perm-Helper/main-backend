package com.melowetty.hsepermhelper.context

import com.melowetty.hsepermhelper.domain.model.context.ExcelTimetableParseContext
import com.melowetty.hsepermhelper.domain.model.context.ParseError

object ExcelTimetableParseContextHolder {
    private val holder = ThreadLocal.withInitial { ExcelTimetableParseContext() }

    fun get(): ExcelTimetableParseContext {
        return holder.get()
    }

    fun clear() {
        holder.remove()
    }

    fun addError(error: ParseError) {
        get().errors.add(error)
    }
}