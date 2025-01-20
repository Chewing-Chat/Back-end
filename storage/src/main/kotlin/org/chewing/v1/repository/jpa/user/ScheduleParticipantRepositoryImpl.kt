package org.chewing.v1.repository.jpa.user

import jakarta.transaction.Transactional
import org.chewing.v1.jpaentity.user.ScheduleParticipantId
import org.chewing.v1.jpaentity.user.ScheduleParticipantJpaEntity
import org.chewing.v1.jparepository.user.ScheduleParticipantJpaRepository
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.ScheduleParticipantRepository
import org.springframework.stereotype.Repository

@Repository
internal class ScheduleParticipantRepositoryImpl(
    private val scheduleParticipantJpaRepository: ScheduleParticipantJpaRepository,
) : ScheduleParticipantRepository {
    override fun appendParticipants(scheduleId: String, userIds: List<UserId>) {
        val entities = userIds.map { userId ->
            ScheduleParticipantJpaEntity.generate(
                userId = userId,
                scheduleId = scheduleId,
            )
        }
        scheduleParticipantJpaRepository.saveAll(entities)
    }

    override fun readsParticipants(
        scheduleIds: List<String>,
        status: ScheduleParticipantStatus,
    ): List<ScheduleParticipant> {
        val entities = scheduleParticipantJpaRepository.findAllByIdScheduleIdInAndStatus(scheduleIds, status)
        return entities.map { it.toParticipant() }
    }

    override fun readParticipant(
        userId: UserId,
        scheduleId: String,
    ): ScheduleParticipant? {
        val scheduleParticipantId = ScheduleParticipantId(userId.id, scheduleId)
        val entity = scheduleParticipantJpaRepository.findById(scheduleParticipantId).orElse(null)
        return entity?.toParticipant()
    }

    override fun readParticipants(scheduleId: String): List<ScheduleParticipant> {
        val entities = scheduleParticipantJpaRepository.findAllByIdScheduleId(scheduleId)
        return entities.map { it.toParticipant() }
    }

    override fun readParticipantScheduleIds(
        userId: UserId,
        status: ScheduleParticipantStatus,
    ): List<String> {
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
    override fun removeAllParticipants(scheduleId: String) {
        val entities = scheduleParticipantJpaRepository.findAllByIdScheduleId(scheduleId)
        entities.forEach {
            it.updateStatus(ScheduleParticipantStatus.DELETED)
        }
    }

    @Transactional
    override fun removeParticipants(scheduleId: String, userIds: List<UserId>) {
        val scheduleParticipantIds = userIds.map { userId ->
            ScheduleParticipantId(userId.id, scheduleId)
        }
        val entities = scheduleParticipantJpaRepository.findAllByIdIn(scheduleParticipantIds)
        entities.forEach {
            it.updateStatus(ScheduleParticipantStatus.DELETED)
        }
    }
}
