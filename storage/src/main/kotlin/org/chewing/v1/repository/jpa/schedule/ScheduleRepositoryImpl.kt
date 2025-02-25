package org.chewing.v1.repository.jpa.schedule

import org.chewing.v1.jpaentity.schedule.ScheduleJpaEntity
import org.chewing.v1.jparepository.schedule.ScheduleJpaRepository
import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.repository.schedule.ScheduleRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@Repository
internal class ScheduleRepositoryImpl(
    private val scheduleJpaRepository: ScheduleJpaRepository,
) : ScheduleRepository {
    override fun append(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent): ScheduleId {
        return scheduleJpaRepository.save(ScheduleJpaEntity.generate(scheduleContent, scheduleTime)).toScheduleInfo().scheduleId
    }

    @Transactional
    override fun remove(scheduleId: ScheduleId) {
        scheduleJpaRepository.findById(scheduleId.id)
            .ifPresent { scheduleEntity ->
                scheduleEntity.updateStatus(ScheduleStatus.DELETED)
                scheduleJpaRepository.save(scheduleEntity)
            }
    }

    override fun update(
        scheduleId: ScheduleId,
        scheduleTime: ScheduleTime,
        scheduleContent: ScheduleContent,
    ) {
        scheduleJpaRepository.findById(scheduleId.id)
            .ifPresent { scheduleEntity ->
                scheduleEntity.updateInfo(scheduleTime, scheduleContent)
                scheduleJpaRepository.save(scheduleEntity)
            }
    }

    override fun reads(scheduleIds: List<ScheduleId>, type: ScheduleType, status: ScheduleStatus): List<ScheduleInfo> {
        val startDateTime = LocalDateTime.of(type.year, type.month, 1, 0, 0)
        val endDateTime = startDateTime
            .with(TemporalAdjusters.firstDayOfNextMonth())
            .minusSeconds(1)
        return scheduleJpaRepository.findSchedules(scheduleIds.map { it.id }, startDateTime, endDateTime, status)
            .map { it.toScheduleInfo() }
    }

    override fun read(scheduleId: ScheduleId, status: ScheduleStatus): ScheduleInfo? {
        return scheduleJpaRepository.findByScheduleIdAndStatus(scheduleId.id, status)?.toScheduleInfo()
    }
}
