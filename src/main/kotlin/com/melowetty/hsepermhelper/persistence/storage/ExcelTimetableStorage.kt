package com.melowetty.hsepermhelper.persistence.storage

import com.melowetty.hsepermhelper.context.JobRunContextHolder
import com.melowetty.hsepermhelper.exception.TimetableByIdNotFoundException
import com.melowetty.hsepermhelper.extension.ScheduleExtensions.Companion.computeHash
import com.melowetty.hsepermhelper.persistence.entity.ExcelTimetableEntity
import com.melowetty.hsepermhelper.persistence.entity.GroupLessonsEntity
import com.melowetty.hsepermhelper.persistence.entity.GroupLessonsEntityId
import com.melowetty.hsepermhelper.persistence.repository.ExcelTimetableRepository
import com.melowetty.hsepermhelper.persistence.repository.GroupLessonsJdbcRepository
import com.melowetty.hsepermhelper.persistence.repository.GroupLessonsRepository
import com.melowetty.hsepermhelper.timetable.model.EducationType
import com.melowetty.hsepermhelper.timetable.model.ExcelTimetable
import com.melowetty.hsepermhelper.timetable.model.InternalTimetableInfo
import com.melowetty.hsepermhelper.timetable.model.impl.GroupBasedLesson
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ExcelTimetableStorage(
    private val excelTimetableRepository: ExcelTimetableRepository,
    private val groupLessonsRepository: GroupLessonsRepository,
    private val groupLessonsJdbcRepository: GroupLessonsJdbcRepository,
) {
    fun getParentTimetables(educationType: EducationType): List<InternalTimetableInfo> {
        return excelTimetableRepository.findAllByVisibleIsTrueAndParentIsTrue(educationType)
            .map { it.toInfo() }
    }

    fun getTimetableFilteredByGroup(id: String, group: String): ExcelTimetable {
        val timetable = getExcelTimetableEntity(id)
        val lessons = getLessonsByTimetableIdAndGroup(id, group)

        return timetable.withLessons(lessons)
    }

    fun getTimetables(ids: List<String>): List<ExcelTimetable> {
        val timetables = excelTimetableRepository.findAllById(ids)
        val lessons = groupLessonsRepository.findAllById_TimetableIdIn(ids).groupBy { it.id.timetableId }

        return timetables.map { timetable ->
            timetable.withLessons(
                lessons[timetable.id]?.map { it.lessons }?.flatten() ?: emptyList()
            )
        }
    }

    fun getTimetable(id: String): ExcelTimetable {
        val timetable = getExcelTimetableEntity(id)
        val lessons = groupLessonsRepository.findAllById_TimetableId(id).map { it.lessons }.flatten()

        return timetable.withLessons(lessons)
    }

    fun getTimetableInfo(id: String): InternalTimetableInfo {
        return getExcelTimetableEntity(id).toInfo()
    }

    fun getAllTimetablesGroups(): Map<EducationType, List<String>> {
        val timetables = excelTimetableRepository.findAll()

        val groupsByTimetableId = groupLessonsRepository.getGroupsAndTimetableIds(timetables.map { it.id })
            .groupBy { it.timetableId }

        return timetables.associate { timetable ->
            timetable.educationType to (groupsByTimetableId[timetable.id]?.map { it.group }?.sorted() ?: emptyList())
        }
    }

    fun getTimetablesInfo(ids: List<String>): List<InternalTimetableInfo> {
        return excelTimetableRepository.findAllById(ids).map {
            it.toInfo()
        }
    }

    fun getTimetablesInfoIdByRunId(runId: String): List<InternalTimetableInfo> {
        val timetables = excelTimetableRepository.findAllByRunId(runId)
        return timetables.map { it.toInfo() }
    }

    @Transactional
    fun showTimetables(ids: List<String>) {
        excelTimetableRepository.setVisibleByIds(ids)
    }

    @Transactional
    fun updateTimetable(id: String, timetable: ExcelTimetable) {
        val existsEntity = getExcelTimetableEntity(id)

        val newLessonsHash = timetable.lessons.computeHash()

        groupLessonsRepository.deleteById_TimetableId(id)
        groupAndSaveLessons(timetable)

        val entity = ExcelTimetableEntity(
            id = id,
            number = timetable.number,
            start = timetable.start,
            end = timetable.end,
            type = timetable.type,
            educationType = timetable.educationType,
            isParent = timetable.isParent,
            isVisible = existsEntity.isVisible,
            source = timetable.source,
            lessonsHash = newLessonsHash,
            created = existsEntity.created,
            updated = LocalDateTime.now(),
            runId = JobRunContextHolder.get()?.id ?: existsEntity.runId,
        )

        excelTimetableRepository.save(entity)
    }

    @Transactional
    fun updateTimetablesRunId(ids: List<String>, runId: String) {
        excelTimetableRepository.setRunIdByIds(ids, runId)
    }

    @Transactional
    fun updateTimetablesRunIdByRunId(oldRunId: String, newRunId: String) {
        excelTimetableRepository.setRunIdByOldRunId(oldRunId, newRunId)
    }

    @Transactional
    fun deleteTimetables(ids: List<String>) {
        excelTimetableRepository.deleteAllByIdInBatch(ids)
    }

    @Transactional
    fun deleteTimetable(id: String) {
        validateTimetableId(id)
        excelTimetableRepository.deleteById(id)
    }

    @Transactional
    fun mergeTimetables(timetablesIds: List<String>) {
        val timetables = getTimetablesInfo(timetablesIds)
        val newLessons = groupLessonsRepository.findAllById_TimetableIdIn(timetablesIds).map { it.lessons }.flatten()
        val newLessonsHash = newLessons.computeHash()

        val targetTimetable = timetables.sortedBy { it.start }.reduce { acc, timetable ->
            acc.copy(
                end = maxOf(acc.end, timetable.end)
            )
        }.copy(lessonsHash = newLessonsHash)

        updateTimetableInfo(targetTimetable.id, targetTimetable)

        groupLessonsRepository.deleteGroupsLessonsByTimetableIds(timetablesIds)
        groupAndSaveLessons(newLessons, targetTimetable.id)

        val idsForDelete = timetablesIds.filter { it != targetTimetable.id }
        deleteTimetables(idsForDelete)
    }

    @Transactional
    fun deleteTimetablesByRunId(runId: String) {
        excelTimetableRepository.deleteAllByRunId(runId)
    }

    @Transactional
    fun saveTimetableAsHidden(timetable: ExcelTimetable): ExcelTimetable {
        val id = generateId()
        timetable.id = id

        val runId = JobRunContextHolder.get()?.id
            ?: run {
                logger.warn { "Run job context is not defined" }
                return timetable
            }

        val entity = ExcelTimetableEntity(
            id = id,
            number = timetable.number,
            start = timetable.start,
            end = timetable.end,
            type = timetable.type,
            educationType = timetable.educationType,
            isParent = timetable.isParent,
            isVisible = false,
            source = timetable.source,
            lessonsHash = timetable.lessons.computeHash(),
            created = timetable.created,
            updated = timetable.updated,
            runId = runId,
        )

        excelTimetableRepository.save(entity)
        excelTimetableRepository.flush()

        groupAndSaveLessons(timetable)

        return timetable
    }

    private fun updateTimetableInfo(id: String, timetable: InternalTimetableInfo) {
        val existsEntity = getExcelTimetableEntity(id)

        val entity = ExcelTimetableEntity(
            id = id,
            number = timetable.number,
            start = timetable.start,
            end = timetable.end,
            type = timetable.type,
            educationType = timetable.educationType,
            isParent = timetable.isParent,
            isVisible = existsEntity.isVisible,
            source = timetable.source,
            lessonsHash = timetable.lessonsHash,
            created = existsEntity.created,
            updated = LocalDateTime.now(),
            runId = existsEntity.runId,
        )

        excelTimetableRepository.saveAndFlush(entity)
    }

    private fun getLessonsByTimetableIdAndGroup(timetableId: String, group: String): List<GroupBasedLesson> {
        return groupLessonsRepository.findById_TimetableIdAndId_Group(timetableId, group)?.lessons ?: emptyList()
    }

    private fun getExcelTimetableEntity(id: String): ExcelTimetableEntity {
        return excelTimetableRepository.findById(id).getOrNull()
            ?: throw TimetableByIdNotFoundException(id)
    }

    private fun validateTimetableId(id: String) {
        if (!excelTimetableRepository.existsById(id)) {
            throw TimetableByIdNotFoundException(id)
        }
    }

    private fun groupAndSaveLessons(timetable: ExcelTimetable) {
        groupAndSaveLessons(timetable.lessons, timetable.id())
    }

    private fun groupAndSaveLessons(lessons: List<GroupBasedLesson>, timetableId: String) {
        val entities = lessons.groupBy { it.group }.map { (group, lessons) ->
            val id = GroupLessonsEntityId(
                timetableId = timetableId,
                group = group,
            )
            val lessonsEntity = GroupLessonsEntity(
                id = id,
                lessons = lessons.sortedBy { it.time },
            )

            lessonsEntity
        }

        groupLessonsJdbcRepository.batchInsert(entities)
    }

    private fun ExcelTimetableEntity.toInfo(): InternalTimetableInfo {
        return InternalTimetableInfo(
            id = id,
            number = number,
            type = type,
            educationType = educationType,
            isParent = isParent,
            start = start,
            end = end,
            source = source,
            lessonsHash = lessonsHash,
        )
    }

    private fun ExcelTimetableEntity.withLessons(lessons: List<GroupBasedLesson>): ExcelTimetable {
        return ExcelTimetable(
            id = id,
            number = number,
            type = type,
            educationType = educationType,
            isParent = isParent,
            start = start,
            end = end,
            source = source,
            lessons = lessons.sortedBy { it.time },
        )
    }

    private fun generateId(): String {
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH).lowercase()
    }

    companion object {
        private const val ID_LENGTH = 6

        private val logger = KotlinLogging.logger {  }
    }
}