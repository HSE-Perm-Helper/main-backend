package com.melowetty.hsepermhelper.remote.extension

import com.melowetty.hsepermhelper.domain.model.lesson.Lesson
import com.melowetty.hsepermhelper.domain.model.lesson.LessonType
import com.melowetty.hsepermhelper.domain.model.lesson.ScheduledTime
import com.melowetty.hsepermhelper.remote.utils.DateUtils
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.Location
import net.fortuna.ical4j.model.property.Uid
import net.fortuna.ical4j.model.property.Url

object LessonExtension {
    /**
     * Converts lesson object to VEvent for import in calendar
     *
     * @return converted lesson to VEvent object
     */
    fun Lesson.toVEvent(currentDateTime: LocalDateTime): VEvent {
        if (this.time !is ScheduledTime) {
            throw IllegalArgumentException("Lesson must be scheduled")
        }

        val startDateTime = getLocalDateTimeFromTimeAsString((time as ScheduledTime).date, time.startTime).toInstant(DateUtils.zoneOffset)
        val endDateTime = getLocalDateTimeFromTimeAsString((time as ScheduledTime).date, time.endTime).toInstant(DateUtils.zoneOffset)

        val event = VEvent(
            startDateTime, endDateTime,
            lessonType.toCalendarTitle(subject)
        )
        val descriptionLines: MutableList<String> = mutableListOf()

        if (subGroup != null) {
            descriptionLines.add("$subGroup подгруппа")
        }

        if (lecturer != null) {
            descriptionLines.add(lecturer!!)
        }

        if (isOnline()) {
            event.add(Location("Онлайн"))
            if (!links.isNullOrEmpty()) {

                event.add(Url(URI.create(links!![0])))

                if (links!!.size > 1) {
                    descriptionLines.add("Дополнительные ссылки на пару: ")
                    links!!.subList(1, links!!.size).forEach { descriptionLines.add(it) }
                }
            }

        } else {
            if (places.isNullOrEmpty()) {
                if (lessonType == LessonType.COMMON_MINOR) {
                    descriptionLines.add(
                        "Информацию о времени и ссылке на майнор узнайте " +
                                "подробнее в HSE App X или в системе РУЗ"
                    )
                }

            } else {
                event.add(Location(places!!.joinToString(" · ") {
                    "${it.office}, ${it.building} корпус"
                }
                ))
            }
        }

        if (additionalInfo?.isNotEmpty() == true) {
            descriptionLines.add(
                "\n" +
                        "Дополнительная информация: ${additionalInfo!!.joinToString("\n")}"
            )
        }


        descriptionLines.add(
            "\n" +
                    "Последнее обновление: ${currentDateTime.format(DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_PATTERN))}"
        )

        val id = UUID.nameUUIDFromBytes(hashCode().toString().toByteArray())
        event.add(Uid(id.toString()))
        event.add(
            Description(
                descriptionLines.joinToString("\n")
            )
        )
        return event
    }

    private fun getLocalDateTimeFromTimeAsString(date: LocalDate, time: String): LocalDateTime {
        val dividedTime = time.split(":").map { it.toInt() }
        val localTime = LocalTime.of(dividedTime[0], dividedTime[1])
        return LocalDateTime.of(date, localTime)
    }

    private val LessonType.calendarPattern: String
        get() = when (this) {
            LessonType.INDEPENDENT_EXAM -> "НЭ · {subject}"
            LessonType.UNDEFINED_AED   -> "ДОЦ · {subject}"
            LessonType.CONSULT         -> "{subject}"

            LessonType.COMMON_MINOR,
            LessonType.COMMON_ENGLISH,
            LessonType.ENGLISH         -> "{type}"

            else -> "{type} · {subject}"
        }

    private fun LessonType.toCalendarTitle(subject: String): String {
        return calendarPattern
            .replace("{type}", type)
            .replace("{subject}", subject)
    }

}