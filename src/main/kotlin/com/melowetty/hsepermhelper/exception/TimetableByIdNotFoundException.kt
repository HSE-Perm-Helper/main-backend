package com.melowetty.hsepermhelper.exception

import org.springframework.http.HttpStatus

class TimetableByIdNotFoundException(
    val id: String
) : CustomException("Timetable by id $id not found", HttpStatus.NOT_FOUND)