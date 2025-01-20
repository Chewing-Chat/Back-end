package org.chewing.v1.repository.jpa.user

import jakarta.transaction.Transactional
import org.chewing.v1.jpaentity.user.ScheduleParticipantId
import org.chewing.v1.jpaentity.user.ScheduleParticipantJpaEntity
import org.chewing.v1.jparepository.user.ScheduleParticipantJpaRepository
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.ScheduleParticipantRepository
import org.springframework.stereotype.Repository

@Repository
internal class ScheduleParticipantRepositoryImpl(
    private val scheduleParticipantJpaRepository: ScheduleParticipantJpaRepository,
) : ScheduleParticipantRepository {
    override fun appendParticipants(scheduleId: ScheduleId, userIds: List<UserId>) {
        val entities = userIds.map { userId ->
            ScheduleParticipantJpaEntity.generate(
                userId = userId,
                scheduleId = scheduleId,
            )
        }
        scheduleParticipantJpaRepository.saveAll(entities)
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

    override fun readParticipants(scheduleId: ScheduleId): List<ScheduleParticipant> {
        val entities = scheduleParticipantJpaRepository.findAllByIdScheduleId(scheduleId.id)
        return entities.map { it.toParticipant() }
    }

    override fun readParticipantScheduleIds(
        userId: UserId,
        status: ScheduleParticipantStatus,
    ): List<ScheduleId> {
        val entities = scheduleParticipantJpaRepository.findAllByIdUserIdAndStatus(userId.id, status)
        return entities.map { it.toParticipant().scheduleId }
    }

    @Transactional
    override fun removeParticipated(userId: UserId) {
        val entities = scheduleParticipantJpaRepository.findAllByIdUserId(userId.id)
        entities.forEach {
            it.updateStatus(ScheduleParticipantStatus.DELETED)
        }
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
}
