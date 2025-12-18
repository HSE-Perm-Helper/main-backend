package com.melowetty.hsepermhelper.persistence.repository

import com.melowetty.hsepermhelper.persistence.entity.GroupLessonsEntity
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import tools.jackson.databind.ObjectMapper

@Repository
class GroupLessonsJdbcRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper
) {
    fun batchInsert(entities: List<GroupLessonsEntity>) {
        val sql = """
            INSERT INTO group_lessons (timetable_id, "group", lessons)
            VALUES (:timetableId, :group, :lessons::jsonb)
        """

        val batchParams = entities.map { entity ->
            mapOf(
                "timetableId" to entity.id.timetableId,
                "group" to entity.id.group,
                "lessons" to objectMapper.writeValueAsString(entity.lessons)
            )
        }

        jdbcTemplate.batchUpdate(sql, batchParams.toTypedArray())
    }
}