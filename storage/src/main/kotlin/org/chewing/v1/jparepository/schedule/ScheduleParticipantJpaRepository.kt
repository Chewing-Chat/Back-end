package org.chewing.v1.jparepository.schedule

import org.chewing.v1.jpaentity.user.ScheduleParticipantId
import org.chewing.v1.jpaentity.schedule.ScheduleParticipantJpaEntity
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.springframework.data.jpa.repository.JpaRepository

internal interface ScheduleParticipantJpaRepository :
    JpaRepository<ScheduleParticipantJpaEntity, ScheduleParticipantId> {
    fun findAllByIdScheduleIdInAndStatus(scheduleIds: List<String>, status: ScheduleParticipantStatus): List<ScheduleParticipantJpaEntity>
    fun findAllByIdUserIdAndStatus(userId: String, status: ScheduleParticipantStatus): List<ScheduleParticipantJpaEntity>
    fun findAllByIdUserId(userId: String): List<ScheduleParticipantJpaEntity>
    fun findAllByIdScheduleId(scheduleId: String): List<ScheduleParticipantJpaEntity>
    fun findAllByIdScheduleIdAndStatus(scheduleId: String, status: ScheduleParticipantStatus): List<ScheduleParticipantJpaEntity>
    fun findAllByIdIn(ids: List<ScheduleParticipantId>): List<ScheduleParticipantJpaEntity>
}
