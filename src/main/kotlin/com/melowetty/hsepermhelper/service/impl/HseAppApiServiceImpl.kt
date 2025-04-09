package com.melowetty.hsepermhelper.service.impl

import com.fasterxml.jackson.annotation.JsonProperty
import com.melowetty.hsepermhelper.config.HseAppApiCacheConfig
import com.melowetty.hsepermhelper.domain.model.hseapp.HseAppLesson
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.service.HseAppApiService
import com.melowetty.hsepermhelper.util.LinkUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class HseAppApiServiceImpl(
    @Qualifier("hse-app")
    private val restTemplate: RestTemplate,
    @Qualifier("hse-app-retryer")
    private val retryTemplate: RetryTemplate
): HseAppApiService {
    companion object {
        private val dateFormat = DateTimeFormatter.ISO_DATE
        private const val HSE_APP_X_MINOR_BUILDING_ID = 6789

        fun normalizeLecturer(lecturer: String): String {
            val words = lecturer.split(" ")

            if (words.size == 1) return lecturer

            return (listOf(words.first()) + words.subList(1, words.size)
                .mapNotNull { it.firstOrNull() }
                .map { "$it." }.toList())
                .joinToString(" ")
        }

        fun normalizeSubject(subject: String): String {
            return subject.replace("(рус)", "").trim()
        }
    }

    @Cacheable(cacheNames = [HseAppApiCacheConfig.HSE_APP_LESSONS_CACHE])
    override fun getLessons(studentEmail: String, from: LocalDate, to: LocalDate): List<HseAppLesson> {
        return directGetLessons(studentEmail, from, to)
    }

    @CachePut(cacheNames = [HseAppApiCacheConfig.HSE_APP_LESSONS_CACHE])
    override fun directGetLessons(
        studentEmail: String,
        from: LocalDate,
        to: LocalDate
    ): List<HseAppLesson> {
        val url: String = UriComponentsBuilder.fromPath("/v3/ruz/lessons")
            .queryParam("email", studentEmail)
            .queryParam("start", from.format(dateFormat))
            .queryParam("end", to.format(dateFormat))
            .encode()
            .toUriString()

        val lessons = retryTemplate.execute<Array<HseAppApiLesson>, HttpServerErrorException> {
            restTemplate.getForObject(url, Array<HseAppApiLesson>::class.java)
                ?: throw RuntimeException("Произошла ошибка во время получения пар из внешнего источника")
        }

        return lessons.map {
            val streamLinks = processStreamLinks(it.streamLinks, it.note)
            val note = processNote(streamLinks, it.note)

            return@map HseAppLesson(
                subject = normalizeSubject(it.discipline),
                subjectLink = it.disciplineLink,
                dateStart = it.dateStart,
                dateEnd = it.dateEnd,
                streamLinks = streamLinks.ifEmpty { null },
                lecturers = it.lecturerProfiles.map { it.fullName }.map { normalizeLecturer(it) },
                note = note,
                type = getLessonType(it.type),
                auditorium = it.auditorium,
                isMinor = it.auditoriumId == HSE_APP_X_MINOR_BUILDING_ID
            )
        }
    }

    fun getLessonType(value: String): LessonType {
        val typeByValue: Map<String, LessonType> = LessonType.values().associateBy { it.type }

        return when(value.lowercase()) {
            "лекция" -> LessonType.LECTURE
            "семинары" -> LessonType.SEMINAR
            "экзамен" -> LessonType.EXAM
            else -> typeByValue[value.lowercase()] ?: LessonType.LECTURE
        }
    }

    fun processStreamLinks(streamLinks: List<StreamLink>?, note: String?): List<String> {
        val streamLinks: List<String> = (streamLinks ?: listOf())
            .stream().map { it.link }.toList()

        val links = note?.let { it ->
            LinkUtils.LINK_REGEX.findAll(it)
                .map { it.value }
                .toList()
        } ?: listOf()

        return streamLinks + links
    }

    fun processNote(streamLinks: List<String>, note: String?): String? {
        var note = note
        streamLinks.forEach { note = note?.replace(it, "") }

        if (note?.isBlank() == true) {
            note = null
        }

        return note
    }

    data class HseAppApiLesson(
        val type: String,
        @JsonProperty("date_start")
        val dateStart: LocalDateTime,
        @JsonProperty("date_end")
        val dateEnd: LocalDateTime,
        val discipline: String,
        @JsonProperty("discipline_link")
        val disciplineLink: String,
        val note: String?,
        val streamLinks: List<StreamLink>?,
        @JsonProperty("lecturer_profiles")
        val lecturerProfiles: List<LecturerProfile>,
        @JsonProperty("auditorium_id")
        val auditoriumId: Int,
        val auditorium: String?
    )

    data class StreamLink(
        val link: String
    )

    data class LecturerProfile(
        @JsonProperty("full_name")
        val fullName: String
    )
}