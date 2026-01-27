package com.melowetty.hsepermhelper.domain.model.context

data class ExcelTimetableParseContext(
    val errors: MutableList<ParseError> = mutableListOf(),
)

data class ParseError(
    val sheet: String,
    val cell: String,
    val cellValue: String,
    val exception: Exception,
)
