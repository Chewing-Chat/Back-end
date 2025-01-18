package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.user.ScheduleJpaEntity
import org.chewing.v1.jparepository.user.ScheduleJpaRepository
import org.chewing.v1.model.schedule.*
import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@Repository
internal class ScheduleRepositoryImpl(
    private val scheduleJpaRepository: ScheduleJpaRepository,
) : ScheduleRepository {
    override fun append(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent): String {
        return scheduleJpaRepository.save(ScheduleJpaEntity.generate(scheduleContent, scheduleTime)).toScheduleInfo().id
    }

    @Transactional
    override fun remove(scheduleId: String) {
        scheduleJpaRepository.findById(scheduleId)
            .ifPresent {
                    scheduleEntity ->
                scheduleEntity.updateStatus(ScheduleStatus.DELETED)
                scheduleJpaRepository.save(scheduleEntity)
            }
    }

    override fun update(
        scheduleId: String,
        scheduleTime: ScheduleTime,
        scheduleContent: ScheduleContent,
    ) {
        scheduleJpaRepository.findById(scheduleId)
            .ifPresent {
                    scheduleEntity ->
                scheduleEntity.updateInfo(scheduleTime, scheduleContent)
                scheduleJpaRepository.save(scheduleEntity)
            }
    }

    override fun reads(scheduleIds: List<String>, type: ScheduleType, status: ScheduleStatus): List<ScheduleInfo> {
        val startDateTime = LocalDateTime.of(type.year, type.month, 1, 0, 0)
        val endDateTime = startDateTime
            .with(TemporalAdjusters.firstDayOfNextMonth()) // 다음 달의 첫 날로 설정
            .minusSeconds(1) // 1초 전으로 설정
        return scheduleJpaRepository.findSchedules(scheduleIds, startDateTime, endDateTime, status)
            .map { it.toScheduleInfo() }
    }
}
