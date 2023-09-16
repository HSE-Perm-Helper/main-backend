package com.melowetty.hsepermhelper.utils

import Schedule
import com.melowetty.hsepermhelper.models.Lesson
import com.melowetty.hsepermhelper.models.LessonType
import jakarta.servlet.http.HttpServletRequest
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.property.*
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.AntPathMatcher
import org.springframework.web.servlet.HandlerMapping
import java.time.Duration


class FileUtils {
    companion object {
        /**
         * Gets file download response entity from resource
         *
         * @param resource file
         * @param fileName must contains file extension
         * @return response entity with file
         */
        fun getFileDownloadResponse(resource: Resource, fileName: String): ResponseEntity<Resource> {
            val header = HttpHeaders()
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${fileName}")
            header.add("Cache-Control", "no-cache, no-store, must-revalidate")
            header.add("Pragma", "no-cache")
            header.add("Expires", "0")
            if(fileName.endsWith(".ics")) {
                return ResponseEntity
                    .ok()
                    .headers(header)
                    .contentType(MediaType.parseMediaType("text/calendar"))
                    .body(resource)
            }
            return ResponseEntity
                .ok()
                .headers(header)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource)
        }

        /**
         * Convert list of schedules to calendar file as resource for downloading
         *
         * @param schedules list of schedules, whose will be converted
         * @return calendar file as resource
         */
        fun convertSchedulesToCalendarFile(schedules: List<Schedule>): Resource {
            val calendar = Calendar().withDefaults().fluentTarget
            calendar.add(ProdId("-//HSE Perm Schedule Bot//Расписание пар 1.0//RU"))

            val name = "Расписание"
            calendar.add(Name(name))
            calendar.add(XProperty("X-WR-CALNAME", name))

            calendar.add(Method(Method.VALUE_PUBLISH))

            val description = "Расписание пар в НИУ ВШЭ - Пермь by HSE Perm Schedule Bot"
            calendar.add(Description(description))
            calendar.add(XProperty("X-WR-CALDESC", description))

            val color = Color()
            color.value = "0:71:187"
            calendar.add(color)
            calendar.add(XProperty("X-APPLE-CALENDAR-COLOR", "#0047BB"))

            calendar.add(RefreshInterval(null, Duration.ofHours(1)))
            val allLessons = mutableListOf<Lesson>()
            schedules.forEach { schedule ->
                schedule.lessons
                    .flatMap { it.value }
                    .forEach lessonsForeach@ {
                        allLessons.add(it)
                }
            }
            LessonUtils.clearRepeats(allLessons.sorted())
                .forEach {
                    calendar.add(it.toVEvent())
                }
            val calendarByte = calendar.toString().toByteArray()
            return ByteArrayResource(calendarByte)
        }

        fun extractFilePath(request: HttpServletRequest): String {
            val path = request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
            ) as String
            val bestMatchPattern = request.getAttribute(
                HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE
            ) as String
            val apm = AntPathMatcher()
            return apm.extractPathWithinPattern(bestMatchPattern, path)
        }
    }
}