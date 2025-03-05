package org.chewing.v1.repository.jpa.schedule

import jakarta.transaction.Transactional
import org.chewing.v1.jpaentity.user.ScheduleParticipantId
import org.chewing.v1.jpaentity.schedule.ScheduleParticipantJpaEntity
import org.chewing.v1.jparepository.schedule.ScheduleParticipantJpaRepository
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.schedule.ScheduleParticipantRepository
import org.springframework.stereotype.Repository

@Repository
internal class ScheduleParticipantRepositoryImpl(
    private val scheduleParticipantJpaRepository: ScheduleParticipantJpaRepository,
) : ScheduleParticipantRepository {
    override fun appendParticipants(scheduleId: ScheduleId, userIds: List<UserId>) {
        val entities = userIds.map { userId ->
            ScheduleParticipantJpaEntity.Companion.generate(
                userId = userId,
                scheduleId = scheduleId,
                role = ScheduleParticipantRole.PARTICIPANT,
            )
        }
        scheduleParticipantJpaRepository.saveAll(entities)
    }

    override fun appendOwner(scheduleId: ScheduleId, userId: UserId) {
        val entity = ScheduleParticipantJpaEntity.Companion.generate(
            userId = userId,
            scheduleId = scheduleId,
            role = ScheduleParticipantRole.OWNER,
        )
        scheduleParticipantJpaRepository.save(entity)
    }

    override fun readsParticipants(
        scheduleIds: List<ScheduleId>,
        status: ScheduleParticipantStatus,
    ): List<ScheduleParticipant> {
        val entities = scheduleParticipantJpaRepository.findAllByIdScheduleIdInAndStatus(scheduleIds.map { it.id }, status)
        return entities.map { it.toParticipant() }
    }

    override fun readParticipant(
        userId: UserId,
        scheduleId: ScheduleId,
    ): ScheduleParticipant? {
        val scheduleParticipantId = ScheduleParticipantId(userId.id, scheduleId.id)
        val entity = scheduleParticipantJpaRepository.findById(scheduleParticipantId).orElse(null)
        return entity?.toParticipant()
    }

    override fun readAllParticipants(scheduleId: ScheduleId): List<ScheduleParticipant> {
        val entities = scheduleParticipantJpaRepository.findAllByIdScheduleId(scheduleId.id)
        return entities.map { it.toParticipant() }
    }

    override fun readParticipants(userId: UserId, status: ScheduleParticipantStatus): List<ScheduleId> {
        val entities = scheduleParticipantJpaRepository.findAllByIdUserIdAndStatus(userId.id, status)
        return entities.map { it.toParticipant().scheduleId }
    }

    override fun readParticipantScheduleIds(
        userId: UserId,
        status: ScheduleParticipantStatus,
    ): List<ScheduleId> {
        val entities = scheduleParticipantJpaRepository.findAllByIdUserIdAndStatus(userId.id, status)
        return entities.map { it.toParticipant().scheduleId }
    }

    @Transactional
    override fun removeParticipated(userId: UserId): List<ScheduleId> {
        val entities = scheduleParticipantJpaRepository.findAllByIdUserId(userId.id)
        entities.forEach {
            it.updateStatus(ScheduleParticipantStatus.DELETED)
        }
        return entities.map { it.toParticipant().scheduleId }
    }

    @Transactional
    override fun removeAllParticipants(scheduleId: ScheduleId) {
        val entities = scheduleParticipantJpaRepository.findAllByIdScheduleId(scheduleId.id)
        entities.forEach {
            it.updateStatus(ScheduleParticipantStatus.DELETED)
        }
    }

    @Transactional
    override fun removeParticipants(scheduleId: ScheduleId, userIds: List<UserId>) {
        val scheduleParticipantIds = userIds.map { userId ->
            ScheduleParticipantId(userId.id, scheduleId.id)
        }
        val entities = scheduleParticipantJpaRepository.findAllByIdIn(scheduleParticipantIds)
        entities.forEach {
            it.updateStatus(ScheduleParticipantStatus.DELETED)
        }
    }

    @Transactional
    override fun removeParticipant(scheduleId: ScheduleId, userId: UserId) {
        val scheduleParticipantId = ScheduleParticipantId(userId.id, scheduleId.id)
        val entity = scheduleParticipantJpaRepository.findById(scheduleParticipantId).orElse(null)
        entity!!.updateStatus(ScheduleParticipantStatus.DELETED)
    }

    @Transactional
    override fun updateParticipants(
        scheduleId: ScheduleId,
        userIds: List<UserId>,
        status: ScheduleParticipantStatus,
    ) {
        val scheduleParticipantIds = userIds.map { userId ->
            ScheduleParticipantId(userId.id, scheduleId.id)
        }
        val entities = scheduleParticipantJpaRepository.findAllByIdIn(scheduleParticipantIds)
        entities.forEach {
            it.updateStatus(status)
        }
    }
}
