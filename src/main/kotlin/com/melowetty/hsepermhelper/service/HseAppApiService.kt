package com.melowetty.hsepermhelper.service

import com.melowetty.hsepermhelper.model.hseapp.HseAppLesson
import java.time.LocalDate

interface HseAppApiService {
    fun getLessons(studentEmail: String, from: LocalDate, to: LocalDate): List<HseAppLesson>
    fun directGetLessons(studentEmail: String, from: LocalDate, to: LocalDate): List<HseAppLesson>
}