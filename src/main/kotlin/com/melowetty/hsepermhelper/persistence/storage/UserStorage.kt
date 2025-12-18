package com.melowetty.hsepermhelper.persistence.storage

import com.melowetty.hsepermhelper.domain.Pageable
import com.melowetty.hsepermhelper.domain.model.Field
import com.melowetty.hsepermhelper.persistence.entity.UserEntity
import com.melowetty.hsepermhelper.domain.model.user.EducationGroupEntity
import com.melowetty.hsepermhelper.domain.model.user.UserChangeRequest
import com.melowetty.hsepermhelper.domain.model.user.UserRole
import com.melowetty.hsepermhelper.exception.user.UserByIdNotFoundException
import com.melowetty.hsepermhelper.persistence.projection.EducationGroupRecord
import com.melowetty.hsepermhelper.persistence.projection.UserRecord
import com.melowetty.hsepermhelper.persistence.repository.UserRepository
import com.melowetty.hsepermhelper.timetable.model.EducationType
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Tuple
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull
import org.springframework.beans.factory.annotation.Autowired

@Component
class UserStorage(
    private val userRepository: UserRepository,
    private val hiddenLessonStorage: HiddenLessonStorage,
) {
    @Autowired
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    fun existsUserById(id: UUID): Boolean = userRepository.existsById(id)
    fun existsUserByTelegramId(telegramId: Long): Boolean = userRepository.existsByTelegramId(telegramId)
    fun existsUserByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    fun createUser(
        telegramId: Long,
        group: String,
        educationType: EducationType,
        isEnabledNewScheduleNotifications: Boolean,
        isEnabledChangedScheduleNotifications: Boolean,
        isEnabledComingLessonsNotifications: Boolean,
        roles: List<UserRole>,
    ): UserRecord {
        val user = UserEntity(
            telegramId = telegramId,
            educationGroup = EducationGroupEntity(
                group = group,
                educationType = educationType,
            ),
            isEnabledNewScheduleNotifications = isEnabledNewScheduleNotifications,
            isEnabledChangedScheduleNotifications = isEnabledChangedScheduleNotifications,
            isEnabledComingLessonsNotifications = isEnabledComingLessonsNotifications,
            roles = roles
        )

        return addAdditionalFields(UserRecord.from(userRepository.save(user)))
    }

    fun changeUser(userId: UUID, changeRequest: UserChangeRequest): UserRecord {
        val user = userRepository.findById(userId).getOrElse {
            throw UserByIdNotFoundException(userId)
        }

        val updatedUser = user.apply {
            if (changeRequest.email is Field.Set) this.email = changeRequest.email.value

            if (changeRequest.group is Field.Set) this.educationGroup =
                this.educationGroup.copy(group = changeRequest.group.value)

            if (changeRequest.educationType is Field.Set) this.educationGroup =
                this.educationGroup.copy(educationType = changeRequest.educationType.value)

            if (changeRequest.isEnabledNewScheduleNotifications is Field.Set) this.isEnabledNewScheduleNotifications =
                    changeRequest.isEnabledNewScheduleNotifications.value

            if (changeRequest.isEnabledChangedScheduleNotifications is Field.Set) this.isEnabledChangedScheduleNotifications =
                    changeRequest.isEnabledChangedScheduleNotifications.value

            if (changeRequest.isEnabledComingLessonsNotifications is Field.Set) this.isEnabledComingLessonsNotifications =
                    changeRequest.isEnabledComingLessonsNotifications.value
        }

        val savedUser = userRepository.save(updatedUser)

        return addAdditionalFields(UserRecord.from(savedUser))
    }

    fun getUserIdByTelegramId(telegramId: Long): UUID? = userRepository.getIdByTelegramId(telegramId)

    fun getUsersById(ids: List<UUID>): List<UserRecord> {
        val pureUsers = userRepository.findAllById(ids).map { UserRecord.from(it) }
        val hiddenLessons = hiddenLessonStorage.getUsersHiddenLessons(ids)
        val roles = loadUserRoles(ids)

        return pureUsers.map { user ->
            user.copy(
                roles = roles[user.id].orEmpty(),
                hiddenLessons = hiddenLessons[user.id].orEmpty(),
            )
        }
    }

    @Suppress("LongParameterList")
    fun findUsersAfterId(
        lastId: UUID? = null,
        size: Int = 500,
        educationGroup: String? = null,
        educationType: EducationType? = null,
        isEnabledNewSchedule: Boolean? = null,
        isEnabledChangedSchedule: Boolean? = null,
        isEnabledComingLessons: Boolean? = null,
        options: Options = Options(),
    ): Pageable<UserRecord> {
        val baseUsers = loadBaseUsers(lastId, educationGroup, educationType, isEnabledNewSchedule, isEnabledChangedSchedule, isEnabledComingLessons, size)
        if (baseUsers.isEmpty()) return Pageable(emptyList(), null)

        val userIds = baseUsers.map { it.id }

        val roles = if (options.withRoles) loadUserRoles(userIds) else emptyMap()
        val hiddenLessons = if (options.withHiddenLessons) hiddenLessonStorage.getUsersHiddenLessons(userIds) else emptyMap()

        val result = baseUsers.map { user ->
            user.copy(
                roles = roles[user.id].orEmpty(),
                hiddenLessons = hiddenLessons[user.id].orEmpty(),
            )
        }

        return Pageable(result, result.lastOrNull()?.id)
    }

    fun findUserById(id: UUID): UserRecord? {
        return userRepository.findById(id)
            .map { UserRecord.from(it) }.getOrNull()
            ?.let {
                addAdditionalFields(it)
            }
    }

    fun findUserByTelegramId(telegramId: Long): UserRecord? {
        return userRepository.findByTelegramId(telegramId)
            .map { UserRecord.from(it) }.getOrNull()
            ?.let {
                addAdditionalFields(it)
            }
    }

    private fun addAdditionalFields(user: UserRecord): UserRecord = user.copy(
        roles = loadUserRoles(user.id),
        hiddenLessons = hiddenLessonStorage.getUserHiddenLessons(user.id),
    )

    private fun loadBaseUsers(
        lastId: UUID?,
        educationGroup: String? = null,
        educationType: EducationType?,
        isEnabledNewSchedule: Boolean?,
        isEnabledChangedSchedule: Boolean?,
        isEnabledComingLessons: Boolean?,
        size: Int,
    ): List<UserRecord> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createTupleQuery()
        val root = query.from(UserEntity::class.java)
        val educationGroupNode = root.get<EducationGroupEntity>("educationGroup")

        val predicates = mutableListOf<Predicate>()

        if (lastId != null) {
            predicates.add(cb.greaterThan(root.get("id"), lastId))
        }
        if (educationGroup != null) {
            predicates.add(cb.equal(educationGroupNode.get<String>("group"), educationGroup))
        }
        if (educationType != null) {
            predicates.add(cb.equal(educationGroupNode.get<EducationType>("educationType"), educationType))
        }
        if (isEnabledNewSchedule != null) {
            predicates.add(cb.equal(root.get<Boolean>("isEnabledNewScheduleNotifications"), isEnabledNewSchedule))
        }
        if (isEnabledChangedSchedule != null) {
            predicates.add(cb.equal(root.get<Boolean>("isEnabledChangedScheduleNotifications"), isEnabledChangedSchedule))
        }
        if (isEnabledComingLessons != null) {
            predicates.add(cb.equal(root.get<Boolean>("isEnabledComingLessonsNotifications"), isEnabledComingLessons))
        }

        query.multiselect(
            root.get<UUID>("id"),
            root.get<Long>("telegramId"),
            root.get<String>("email"),
            educationGroupNode.get<String>("group"),
            educationGroupNode.get<EducationType>("educationType"),
            root.get<Boolean>("isEnabledNewScheduleNotifications"),
            root.get<Boolean>("isEnabledChangedScheduleNotifications"),
            root.get<Boolean>("isEnabledComingLessonsNotifications"),
            root.get<LocalDateTime>("createdDate"),
        )
            .where(cb.and(*predicates.toTypedArray()))
            .orderBy(cb.asc(root.get<UUID>("id")))

        val typedQuery = entityManager.createQuery(query)
        typedQuery.firstResult = 0
        typedQuery.maxResults = size

        val tuples = typedQuery.resultList

        return tuples.map { tuple ->
            UserRecord(
                id = tuple[0] as UUID,
                telegramId = tuple[1] as Long,
                email = tuple[2] as String?,
                educationGroup = EducationGroupRecord(
                    group = tuple[3] as String,
                    educationType = tuple[4] as EducationType,
                ),
                isEnabledNewScheduleNotifications = tuple[5] as Boolean,
                isEnabledChangedScheduleNotifications = tuple[6] as Boolean,
                isEnabledComingLessonsNotifications = tuple[7] as Boolean,
                createdDate = tuple[8] as LocalDateTime,
            )
        }
    }

    private fun loadUserRoles(userIds: List<UUID>): Map<UUID, List<UserRole>> {
        if (userIds.isEmpty()) return emptyMap()

        val sql = """
            SELECT user_id, role 
            FROM user_role 
            WHERE user_id IN (?1)
        """.trimIndent()

        val rows = entityManager.createNativeQuery(sql, Tuple::class.java)
            .setParameter(1, userIds)
            .resultList

        return rows.map { tuple ->
            tuple as Tuple
            val userId = tuple.get("user_id") as UUID
            val role = UserRole.valueOf(tuple.get("role") as String)
            userId to role
        }.groupBy({ it.first }, { it.second })
    }

    private fun loadUserRoles(userId: UUID): List<UserRole> = loadUserRoles(listOf(userId))[userId].orEmpty()

    data class Options(
        val withRoles: Boolean = false,
        val withHiddenLessons: Boolean = false,
    )
}