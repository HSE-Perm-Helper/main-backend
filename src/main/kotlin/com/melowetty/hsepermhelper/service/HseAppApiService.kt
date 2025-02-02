package com.melowetty.hsepermhelper.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.melowetty.hsepermhelper.excel.HseTimetableLessonTypeChecker
import com.melowetty.hsepermhelper.excel.model.ParsedLessonInfo
import com.melowetty.hsepermhelper.model.lesson.HseAppLesson
import com.melowetty.hsepermhelper.model.lesson.Lesson
import com.melowetty.hsepermhelper.model.lesson.LessonType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class HseAppApiService(
    @Qualifier("hse-app")
    private val restTemplate: RestTemplate,
) {
    companion object {
        private val dateFormat = DateTimeFormatter.ISO_DATE
    }

    fun getLessons(studentEmail: String, from: LocalDate, to: LocalDate): List<HseAppLesson> {
        val url: String = UriComponentsBuilder.fromPath("/v3/ruz/lessons")
            .queryParam("email", studentEmail)
            .queryParam("start", from.format(dateFormat))
            .queryParam("end", to.format(dateFormat))
            .encode()
            .toUriString()

        val lessons = restTemplate.getForObject(url, Array<HseAppApiLesson>::class.java)
            ?: throw RuntimeException("Произошла ошибка во время получения пар из внешнего источника")

        val typeByValue: Map<String, LessonType> = LessonType.values().associateBy { it.type }

        return lessons.map {
            var note = it.note
            it.streamLinks?.forEach { note = note?.replace(it.linK, "") }

            if (note?.isBlank() == true) {
                note = null
            }

            return@map HseAppLesson(
                subject = it.discipline,
                subjectLink = it.disciplineLink,
                dateStart = it.dateStart,
                dateEnd = it.dateEnd,
                streamLinks = it.streamLinks?.map { it.linK },
                lecturers = it.lecturerProfiles.map { it.fullName },
                note = note,
                type = typeByValue.get(it.type.lowercase()) ?: LessonType.LECTURE
            )
        }
    }

    data class HseAppApiLesson(
        val type: String,
        @JsonProperty("date_start")
        val dateStart: LocalDateTime,
        @JsonProperty("date_start")
        val dateEnd: LocalDateTime,
        val discipline: String,
        @JsonProperty("discipline_link")
        val disciplineLink: String,
        val note: String?,
        val streamLinks: List<StreamLink>?,
        @JsonProperty("lecturer_profiles")
        val lecturerProfiles: List<LecturerProfile>,

    )

    data class StreamLink(
        val linK: String
    )

    data class LecturerProfile(
        @JsonProperty("full_name")
        val fullName: String
    )
}