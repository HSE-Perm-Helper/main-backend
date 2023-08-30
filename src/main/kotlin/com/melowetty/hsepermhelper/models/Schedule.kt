import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.melowetty.hsepermhelper.models.Lesson
import com.melowetty.hsepermhelper.models.LessonType
import com.melowetty.hsepermhelper.utils.DateUtils
import io.swagger.v3.oas.annotations.media.Schema
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.property.*
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Schema(description = "Расписание")
data class Schedule(
    @Schema(description = "Номер недели", example = "6", nullable = true)
    val weekNumber: Int?,
    val lessons: Map<LocalDate, List<Lesson>> = mapOf(),
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата начала недели", example = "03.09.2023", type = "string")
    val weekStart: LocalDate,
    @JsonFormat(pattern = DateUtils.DATE_PATTERN)
    @Schema(description = "Дата конца недели", example = "10.09.2023", type = "string")
    val weekEnd: LocalDate,
    @Schema(description = "Указатель на является ли неделя сессионной", example = "false")
    val isSession: Boolean = false,
) {
    @JsonGetter("lessons")
    fun getFormattedLessons(): Set<Map.Entry<String, List<Lesson>>> {
        return lessons.mapKeys { it.key.format(DateTimeFormatter.ofPattern(DateUtils.DATE_PATTERN)) }.entries
    }
}
