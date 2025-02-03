package org.chewing.v1.jparepository.schedule

import org.chewing.v1.jpaentity.schedule.ScheduleLogJpaEntity
import org.chewing.v1.jpaentity.user.ScheduleParticipantId
import org.springframework.data.jpa.repository.JpaRepository

internal interface ScheduleLogJpaRepository : JpaRepository<ScheduleLogJpaEntity, ScheduleParticipantId> {
    fun findAllByUserId(userId: String): List<ScheduleLogJpaEntity>
}
