package org.chewing.v1.repository.jpa.schedule

import org.chewing.v1.jpaentity.schedule.ScheduleLogJpaEntity
import org.chewing.v1.jparepository.schedule.ScheduleLogJpaRepository
import org.chewing.v1.model.schedule.ScheduleChangeStatus
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleLog
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.schedule.ScheduleLogRepository
import org.springframework.stereotype.Repository

@Repository
internal class ScheduleLogRepositoryImpl(
    private val scheduleLogJpaRepository: ScheduleLogJpaRepository,
) : ScheduleLogRepository {

    override fun appendLog(
        scheduleId: ScheduleId,
        userId: UserId,
        changeStatus: ScheduleChangeStatus,
    ) {
        val entity = ScheduleLogJpaEntity.generate(userId, scheduleId, changeStatus)
        scheduleLogJpaRepository.save(entity)
    }

    override fun readLogs(userId: UserId): List<ScheduleLog> {
        val entities = scheduleLogJpaRepository.findAllByIdUserId(userId.id)
        return entities.map { it.toLog() }
    }
}
